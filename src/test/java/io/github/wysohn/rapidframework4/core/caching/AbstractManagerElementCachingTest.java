package io.github.wysohn.rapidframework4.core.caching;

import com.google.inject.*;
import io.github.wysohn.rapidframework4.core.database.*;
import io.github.wysohn.rapidframework4.core.database.migration.MigrationHelper;
import io.github.wysohn.rapidframework4.core.inject.annotations.PluginDirectory;
import io.github.wysohn.rapidframework4.core.inject.annotations.PluginLogger;
import io.github.wysohn.rapidframework4.core.inject.factory.IDatabaseFactoryCreator;
import io.github.wysohn.rapidframework4.core.inject.module.GsonSerializerModule;
import io.github.wysohn.rapidframework4.core.inject.module.PluginInfoModule;
import io.github.wysohn.rapidframework4.core.inject.module.TypeAsserterModule;
import io.github.wysohn.rapidframework4.core.main.ManagerConfig;
import io.github.wysohn.rapidframework4.interfaces.caching.IObserver;
import io.github.wysohn.rapidframework4.interfaces.plugin.IShutdownHandle;
import io.github.wysohn.rapidframework4.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework4.interfaces.serialize.ITypeAsserter;
import io.github.wysohn.rapidframework4.testmodules.MockConfigModule;
import io.github.wysohn.rapidframework4.testmodules.MockLoggerModule;
import io.github.wysohn.rapidframework4.testmodules.MockPluginDirectoryModule;
import io.github.wysohn.rapidframework4.testmodules.MockShutdownModule;
import io.github.wysohn.rapidframework4.utils.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import javax.inject.Named;
import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class AbstractManagerElementCachingTest {
    private static Database mockDatabase;
    private static DatabaseFactory factory;
    private static DatabaseFactoryCreator factoryCreator;

    private List<Module> moduleList = new LinkedList<>();


    @Before
    public void init() {
        mockDatabase = mock(Database.class);
        factory = mock(DatabaseFactory.class);
        factoryCreator = mock(DatabaseFactoryCreator.class);

        when(factoryCreator.create(anyString())).thenReturn(factory);
        when(factory.create(anyString(), any(), any())).thenReturn(mockDatabase);

        moduleList.add(new PluginInfoModule("test", "test", "test"));
        moduleList.add(new MockLoggerModule());
        moduleList.add(new MockConfigModule(
                Pair.of("dbType", "test")
        ));
        moduleList.add(new MockPluginDirectoryModule());
        moduleList.add(new MockShutdownModule(() -> {
        }));
        moduleList.add(new GsonSerializerModule());
        moduleList.add(new TypeAsserterModule());
        moduleList.add(new AbstractModule() {
            @Provides
            public IDatabaseFactoryCreator creator(){
                return factoryCreator;
            }
        });
    }

    @Test
    public void testLoad() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        TempManager manager = injector.getInstance(TempManager.class);

        UUID[] uuids = new UUID[]{
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
        };

        when(mockDatabase.getKeys()).thenReturn(Arrays.stream(uuids)
                .collect(Collectors.toSet()));
        when(mockDatabase.load(any(UUID.class))).then(invocation -> {
            UUID key = (UUID) invocation.getArguments()[0];

            for (int i = 0; i < uuids.length; i++) {
                if (key.equals(uuids[i])) {
                    TempValue tempValue = new TempValue(uuids[i]);
                    tempValue.str = "value"+i;
                    return tempValue;
                }
            }

            throw new RuntimeException("Test is not configured correctly.");
        });

        //start manager
        manager.enable();
        manager.load();

        Map<UUID, TempValue> cachedElements = (Map<UUID, TempValue>) Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = (Map<String, UUID>) Whitebox.getInternalState(manager, "nameToKeyMap");
        List observers = (List) Whitebox.getInternalState(manager, "observers");

        for (int i = 0; i < uuids.length; i++) {
            assertEquals(manager, cachedElements.get(uuids[i]).manager);
            assertEquals("value" + i, cachedElements.get(uuids[i]).str);

            Mockito.verify(mockDatabase).load(eq(uuids[i]));

            List<Observer> obs = (List<Observer>) Whitebox.getInternalState(cachedElements.get(uuids[i]), "observers");
            assertTrue(observers.stream().allMatch(obs::contains));
        }
    }

    @Test
    public void getCacheSize() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        TempManager manager = injector.getInstance(TempManager.class);

        when(mockDatabase.load(any())).thenReturn(null);

        //start manager
        manager.enable();
        manager.load();

        UUID[] uuids = new UUID[]{
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
        };

        for (UUID uuid : uuids) {
            manager.getOrNew(uuid);
        }

        assertEquals(5, manager.getCacheSize());
    }

    @Test
    public void getDecached() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        TempManager manager = injector.getInstance(TempManager.class);

        Map<UUID, TempValue> cachedElements = org.powermock.reflect.Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = org.powermock.reflect.Whitebox.getInternalState(manager, "nameToKeyMap");

        //start manager
        manager.enable();
        manager.load();

        UUID uuid = UUID.randomUUID();
        TempValue value = new TempValue(uuid);

        //get (will load from db as it's not loaded yet)
        when(mockDatabase.load(Mockito.eq(uuid))).thenReturn(value);
        assertEquals(value.getKey(), manager.get(uuid)
                .map(CachedElement::getKey)
                .orElse(null));

        //check if cache is updated
        assertEquals(value.getKey(), cachedElements.get(uuid).getKey());

        //end the db life-cycle
        manager.disable();
        Mockito.verify(mockDatabase).load(Mockito.eq(uuid));
    }

    @Test
    public void valueNotExist() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        TempManager manager = injector.getInstance(TempManager.class);

        Map<UUID, TempValue> cachedElements = (Map<UUID, TempValue>) Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = (Map<String, UUID>) Whitebox.getInternalState(manager, "nameToKeyMap");

        UUID uuid = UUID.randomUUID();
        TempValue value = new TempValue(uuid);

        //start manager
        manager.enable();
        manager.load();

        //get
        assertFalse(manager.get(uuid).isPresent());

        //end the db life-cycle
        manager.disable();
        Mockito.verify(mockDatabase).load(Mockito.eq(uuid));
    }

    @Test
    public void getOrNew() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        TempManager manager = injector.getInstance(TempManager.class);

        Map<UUID, TempValue> cachedElements = (Map<UUID, TempValue>) Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = (Map<String, UUID>) Whitebox.getInternalState(manager, "nameToKeyMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.enable();
        manager.load();

        //get (new)
        TempValue value = manager.getOrNew(uuid).orElse(null);
        assertEquals(value, cachedElements.get(uuid));

        //get
        when(mockDatabase.load(Mockito.eq(uuid.toString()))).thenReturn(value);
        assertEquals(value, manager.get(uuid).orElse(null));

        //end the db life-cycle
        manager.disable();
        Mockito.verify(mockDatabase).load(Mockito.eq(uuid)); // cache not exist so try from db
        // new data no longer saves unless required
    }

    @Test
    public void updateKeyString() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        TempManager manager = injector.getInstance(TempManager.class);

        Map<UUID, TempValue> cachedElements = (Map<UUID, TempValue>) Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = (Map<String, UUID>) Whitebox.getInternalState(manager, "nameToKeyMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.enable();
        manager.load();

        //get (new)
        TempValue value = manager.getOrNew(uuid).orElse(null);
        assertEquals(value, cachedElements.get(uuid));

        //update
        value.setStringKey("NewKey");

        //get
        assertEquals(value, manager.get(uuid).orElse(null));
        assertEquals(value, manager.get("NewKey").orElse(null));

        //update2
        value.setStringKey("NewKey2");

        //get2
        assertEquals(value, manager.get(uuid).orElse(null));
        assertEquals(value, manager.get("NewKey2").orElse(null));

        //update3
        value.setStringKey("");

        //get3
        assertEquals(value, manager.get(uuid).orElse(null));
        assertNull(manager.get("").orElse(null));

        //update4
        value.setStringKey(null);

        //get4
        assertEquals(value, manager.get(uuid).orElse(null));
        assertNull(manager.get("NewKey2").orElse(null));

        //end the db life-cycle
        manager.disable();
        Mockito.verify(mockDatabase).load(Mockito.eq(uuid)); // cache not exist so try from db
        // new data no longer saves unless required
    }

    @Test
    public void delete() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        TempManager manager = injector.getInstance(TempManager.class);

        Map<UUID, TempValue> cachedElements = (Map<UUID, TempValue>) Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = (Map<String, UUID>) Whitebox.getInternalState(manager, "nameToKeyMap");
        Map<UUID, String> keyToNameMap = (Map<UUID, String>) Whitebox.getInternalState(manager, "keyToNameMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.enable();
        manager.load();

        //new
        TempValue mockValue = manager.getOrNew(uuid).orElse(null);
        mockValue.setStringKey("SomeName");

        assertEquals(mockValue, cachedElements.get(uuid));
        assertEquals(uuid, nameMap.get("SomeName"));

        //delete
        manager.delete(uuid);
        assertNull(cachedElements.get(uuid));
        assertNull(nameMap.get("SomeName"));
        assertNull(keyToNameMap.get(uuid));

        //end the db life-cycle
        manager.disable();
        Mockito.verify(mockDatabase).getKeys();
        Mockito.verify(mockDatabase).load(Mockito.eq(uuid));
        Mockito.verify(mockDatabase).save(Mockito.eq(uuid), isNull(TempValue.class));
    }

    @Test
    public void reset() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        TempManager manager = injector.getInstance(TempManager.class);

        Map<UUID, TempValue> cachedElements = (Map<UUID, TempValue>) Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = (Map<String, UUID>) Whitebox.getInternalState(manager, "nameToKeyMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.enable();
        manager.load();

        //new
        TempValue mockValue = manager.getOrNew(uuid).orElse(null);
        mockValue.setStr("Initial value");

        Assert.assertEquals(mockValue, cachedElements.get(uuid));

        //change value
        mockValue.setStr("ChangedName");
        Assert.assertEquals("ChangedName", mockValue.str);

        //reset
        manager.reset(mockValue);

        //get current data after reset
        TempValue newMockValue = manager.get(uuid).orElse(null);
        Assert.assertNotNull(cachedElements.get(uuid));
        Assert.assertNotNull(newMockValue);

        //end the db life-cycle
        manager.disable();
        //2 for setStr() and 1 for delete()
        Mockito.verify(mockDatabase, Mockito.times(3))
                .save(Mockito.eq(uuid), Mockito.any());
    }

    @Test(expected = RuntimeException.class)
    public void resetInvalidCache() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        TempManager manager = injector.getInstance(TempManager.class);

        Map<UUID, TempValue> cachedElements = (Map<UUID, TempValue>) Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = (Map<String, UUID>) Whitebox.getInternalState(manager, "nameToKeyMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.enable();
        manager.load();

        //new
        TempValue mockValue = manager.getOrNew(uuid).orElse(null);
        mockValue.setStringKey("Initial value");

        Assert.assertEquals(mockValue, cachedElements.get(uuid));
        Assert.assertEquals(uuid, nameMap.get("Initial value"));

        //change value
        mockValue.setStr("ChangedName");
        Assert.assertEquals("ChangedName", mockValue.str);

        //reset
        manager.reset(mockValue);

        //test with old instance (the observer should be unregistered at this point)
        //it will throw exception if it's still subscribed with observer
        mockValue.setStr("Old instance");
    }

    @Test
    public void forEach2() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        TempManager manager = injector.getInstance(TempManager.class);

        Map<String, TempValue> tempDb = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            UUID uuid = UUID.randomUUID();
            TempValue value = new TempValue(uuid);
            value.str = "Value Changed";
            tempDb.put(uuid.toString(), value);
        }

        doAnswer(invocation -> {
            String key = (String) invocation.getArguments()[0];
            TempValue value = (TempValue) invocation.getArguments()[1];
            tempDb.put(key, value);
            return null;
        }).when(mockDatabase).save(anyString(), any());
        doAnswer(invocation -> {
            String key = (String) invocation.getArguments()[0];
            return tempDb.get(key);
        }).when(mockDatabase).load(anyString());
        when(mockDatabase.getKeys()).thenReturn(tempDb.keySet());

        //start manager
        manager.enable();
        manager.load();

        ExecutorService exec = Executors.newCachedThreadPool();
        tempDb.forEach((uuid, v) -> {
            exec.execute(() -> {
                try {
                    manager.load();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            exec.execute(() -> {
                try {
                    Assert.assertEquals("Value Changed", manager.get(UUID.fromString(uuid))
                            
                            .map(val -> val.str)
                            .orElse(null));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        exec.shutdown();
        exec.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    public void forEachDeadLock() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        TempManager manager = injector.getInstance(TempManager.class);

        Map<String, TempValue> tempDb = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            UUID uuid = UUID.randomUUID();
            TempValue value = new TempValue(uuid);
            value.str = "Value Changed";
            tempDb.put(uuid.toString(), value);
        }

        doAnswer(invocation -> {
            String key = (String) invocation.getArguments()[0];
            TempValue value = (TempValue) invocation.getArguments()[1];
            tempDb.put(key, value);
            return null;
        }).when(mockDatabase).save(anyString(), any());
        doAnswer(invocation -> {
            String key = (String) invocation.getArguments()[0];
            return tempDb.get(key);
        }).when(mockDatabase).load(anyString());
        when(mockDatabase.getKeys()).thenReturn(tempDb.keySet());

        //start manager
        manager.enable();
        manager.load();

        Thread thread = new Thread(() -> manager.forEach(val -> val.setStringKey("Other")));
        Thread thread2 = new Thread(() -> manager.forEach(val -> val.setStringKey("Other2")));
        Thread thread3 = new Thread(() -> manager.forEach(val -> val.setStringKey("Other3")));
        thread.start();
        thread2.start();
        thread3.start();
        tempDb.forEach((uuid, v) -> manager.get(uuid)
                
                .ifPresent(val -> val.setStringKey("Other4")));

        manager.disable();
        thread.join();
        thread2.join();
        thread3.join();
    }

    @Test
    public void forEach() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        TempManager manager = injector.getInstance(TempManager.class);

        //start manager
        manager.enable();
        manager.load();

        //get (new)
        for (int i = 0; i < 100; i++) {
            UUID uuid = UUID.randomUUID();
            TempValue value = manager.getOrNew(uuid).orElse(null);
            value.setStr("SomeName" + i);
        }

        Set<String> names = new HashSet<>();
        manager.forEach(tempValue -> names.add(tempValue.str));
        Assert.assertEquals(100, names.size());

        //end the db life-cycle
        manager.disable();
    }

    @Test
    public void search() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        TempManager manager = injector.getInstance(TempManager.class);

        Map<UUID, TempValue> cachedElements = (Map<UUID, TempValue>) Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = (Map<String, UUID>) Whitebox.getInternalState(manager, "nameToKeyMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.enable();
        manager.load();

        IObserver observer = manager.getObservers().stream().findFirst().orElse(null);

        //new
        TempValue mockValue = manager.getOrNew(uuid).orElse(null);
        mockValue.addObserver(observer);
        mockValue.setStr("Initial value");

        Assert.assertEquals(mockValue, cachedElements.get(uuid));

        //search
        Assert.assertTrue(manager.search(tempValue -> tempValue.str.equals("Other")).isEmpty());
        Assert.assertEquals(1, manager.search(tempValue ->
                tempValue.str.equals("Initial value")).size());
    }

    @Test
    public void testTypeAssertion() {
        Injector injector = Guice.createInjector(moduleList);
        TempManager manager = injector.getInstance(TempManager.class);
    }

    @Test(expected = ProvisionException.class)
    public void testTypeAssertionFail() {
        Injector injector = Guice.createInjector(moduleList);
        TempManager2 manager = injector.getInstance(TempManager2.class);
    }

    @Test
    public void migrateFrom() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        TempManager manager = injector.getInstance(TempManager.class);
        Database databaseFrom = mock(Database.class);

        when(factoryCreator.create("file")).thenReturn(new IDatabaseFactory() {
            @Override
            public <K, V extends CachedElement<K>> IDatabase<K, V> create(String tableName,
                                                                          Class<V> valueType,
                                                                          Function<String, K> strToKey) {
                return databaseFrom;
            }
        });

        // data from
        Map<UUID, TempValue> fromDataMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            UUID uuid = UUID.randomUUID();
            TempValue value = new TempValue(uuid);
            value.str = "from"+i;
            fromDataMap.put(uuid, value);
        }
        when(databaseFrom.getKeys()).thenReturn(fromDataMap.keySet());
        when(databaseFrom.load(any())).then(invocation -> {
            UUID key = (UUID) invocation.getArguments()[0];
            return fromDataMap.get(key);
        });

        // data to (with existing data should be overriden)
        Map<UUID, TempValue> currentDataMap = new HashMap<>();
        int j = 0;
        for (Map.Entry<UUID, TempValue> entry : fromDataMap.entrySet()) {
            if(j > 5)
                break;

            UUID uuid = entry.getKey();
            TempValue value = new TempValue(uuid);
            value.str = "current"+j++;
            currentDataMap.put(uuid, value);
        }
        when(mockDatabase.getKeys()).thenReturn(currentDataMap.keySet());
        when(mockDatabase.load(any())).then(invocation -> {
            UUID key = (UUID) invocation.getArguments()[0];
            return currentDataMap.get(key);
        });

        manager.load();
        manager.enable();

        MigrationHelper<UUID, TempValue, TempValue> helper
                = manager.migrateFrom("file");

        helper.start();
        helper.waitForTermination(5, TimeUnit.SECONDS);

        // are previous data migrated into the manager now?
        fromDataMap.forEach((uuid, tempValue) ->
                                    assertEquals(tempValue, manager.get(uuid).orElse(null)));
        // but is the new data a different instance?
        currentDataMap.forEach((uuid, tempValue) ->
                                    assertNotSame(tempValue, manager.get(uuid).orElse(null)));
    }

    @Singleton
    static class TempManager extends AbstractManagerElementCaching<UUID, TempValue> {

        @Inject
        public TempManager(@Named("pluginName") String pluginName,
                           @PluginLogger Logger logger,
                           ManagerConfig config,
                           @PluginDirectory File pluginDir,
                           IShutdownHandle shutdownHandle,
                           ISerializer serializer,
                           ITypeAsserter asserter,
                           IDatabaseFactoryCreator factoryCreator,
                           Injector injector) {
            super(pluginName,
                  logger,
                  config,
                  pluginDir,
                  shutdownHandle,
                  serializer,
                  asserter,
                  factoryCreator,
                  injector,
                  "TempValue",
                  TempValue.class);
        }

        @Override
        protected UUID fromString(String string) {
            return UUID.fromString(string);
        }

        @Override
        protected TempValue newInstance(UUID key) {
            return new TempValue(key);
        }
    }

    static class TempValue extends CachedElement<UUID> {
        @Inject
        private TempManager manager;

        public Object dummy;
        private String str;

        public TempValue() {
            super((UUID) null);
        }

        public TempValue(TempValue copy){
            super(copy.getKey());
            manager = copy.manager;
            dummy = copy.dummy;
            str = copy.str;
        }

        public TempValue(UUID uuid) {
            super(uuid);
        }

        public TempValue(UUID uuid, String stringKey) {
            super(uuid);
            this.str = stringKey;
        }

        public TempValue(UUID uuid, IObserver observer) {
            super(uuid);
            this.addObserver(observer);
        }

        public TempValue setStr(String str) {
            mutate(() -> this.str = str);
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TempValue value = (TempValue) o;
            return Objects.equals(str, value.str);
        }

        @Override
        public int hashCode() {
            return Objects.hash(str);
        }

        @Override
        public String toString() {
            return "TempValue{" +
                    "str='" + str + '\'' +
                    '}';
        }
    }

    @Singleton
    static class TempManager2 extends AbstractManagerElementCaching<UUID, TempValue2> {

        @Inject
        public TempManager2(@Named("pluginName") String pluginName,
                            @PluginLogger Logger logger,
                            ManagerConfig config,
                            @PluginDirectory File pluginDir,
                            IShutdownHandle shutdownHandle,
                            ISerializer serializer,
                            ITypeAsserter asserter,
                            IDatabaseFactoryCreator factoryCreator,
                            Injector injector) {
            super(pluginName,
                  logger,
                  config,
                  pluginDir,
                  shutdownHandle,
                  serializer,
                  asserter,
                  factoryCreator,
                  injector,
                  "TempValue2",
                  TempValue2.class);
        }

        @Override
        protected UUID fromString(String string) {
            return UUID.fromString(string);
        }

        @Override
        protected TempValue2 newInstance(UUID key) {
            return new TempValue2(key);
        }
    }

    static class TempValue2 extends CachedElement<UUID> {
        @Inject
        private TempManager manager;

        public Object dummy;
        private String str;

        public TempValue2(UUID uuid) {
            super(uuid);
        }
    }
}