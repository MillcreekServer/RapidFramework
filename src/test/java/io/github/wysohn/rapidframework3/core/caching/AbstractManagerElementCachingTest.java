package io.github.wysohn.rapidframework3.core.caching;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.github.wysohn.rapidframework3.core.database.Database;
import io.github.wysohn.rapidframework3.core.database.Databases;
import io.github.wysohn.rapidframework3.core.inject.module.ElementCachingManagerModule;
import io.github.wysohn.rapidframework3.core.inject.module.GsonSerializerModule;
import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.interfaces.caching.IObserver;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework3.modules.MockMainModule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.ref.Reference;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class AbstractManagerElementCachingTest {
    private static Database mockDatabase;

    private MockMainModule mockMainModule;

    private List<Module> moduleList = new LinkedList<>();

    private TempManager manager;
    private IObserver observer;


    @Before
    public void init() {
        mockDatabase = mock(Database.class);
        mockMainModule = new MockMainModule();
        when(mockMainModule.mockConfig.get(eq("dbType"))).thenReturn(Optional.of("file"));

        moduleList.add(new GsonSerializerModule());
        moduleList.add(new ElementCachingManagerModule<>(TempManager.class, TempValue.class));
        moduleList.add(mockMainModule);

        Injector injector = Guice.createInjector(moduleList);
        manager = injector.getInstance(TempManager.class);

        observer = manager.getObservers().stream().findFirst().orElse(null);
    }

    @Test
    public void testLoad() throws Exception {
        UUID[] uuids = new UUID[]{
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
        };

        when(mockDatabase.getKeys()).thenReturn(Arrays.stream(uuids)
                .map(UUID::toString)
                .collect(Collectors.toSet()));
        when(mockDatabase.load(Mockito.anyString())).then(invocation -> {
            String key = (String) invocation.getArguments()[0];

            for (int i = 0; i < uuids.length; i++) {
                if (key.equals(uuids[i].toString())) {
                    return "{\"str\":\"value" + i + "\"}";
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

            Mockito.verify(mockDatabase).load(eq(uuids[i].toString()));

            List<Observer> obs = (List<Observer>) Whitebox.getInternalState(cachedElements.get(uuids[i]), "observers");
            assertTrue(observers.stream().allMatch(obs::contains));
        }
    }

    @Test
    public void getCacheSize() throws Exception {
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
        Map<UUID, TempValue> cachedElements = org.powermock.reflect.Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = org.powermock.reflect.Whitebox.getInternalState(manager, "nameToKeyMap");

        //start manager
        manager.enable();
        manager.load();

        UUID uuid = UUID.randomUUID();
        TempValue value = new TempValue(uuid);

        //get (will load from db as it's not loaded yet)
        when(mockDatabase.load(Mockito.eq(uuid.toString()))).thenReturn("{\"key\": \"" + uuid.toString() + "\"}");
        assertEquals(value.getKey(), manager.get(uuid)
                .map(Reference::get)
                .map(CachedElement::getKey)
                .orElse(null));

        //check if cache is updated
        assertEquals(value.getKey(), cachedElements.get(uuid).getKey());

        //end the db life-cycle
        manager.disable();
        Mockito.verify(mockDatabase).load(Mockito.eq(uuid.toString()));
    }

    @Test
    public void valueNotExist() throws Exception {
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
        Mockito.verify(mockDatabase).load(Mockito.eq(uuid.toString()));
    }

    @Test
    public void getOrNew() throws Exception {
        Map<UUID, TempValue> cachedElements = (Map<UUID, TempValue>) Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = (Map<String, UUID>) Whitebox.getInternalState(manager, "nameToKeyMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.enable();
        manager.load();

        //get (new)
        TempValue value = manager.getOrNew(uuid).map(Reference::get).orElse(null);
        assertEquals(value, cachedElements.get(uuid));

        //get
        when(mockDatabase.load(Mockito.eq(uuid.toString()))).thenReturn("{\"key\": \"" + uuid.toString() + "\"}");
        assertEquals(value, manager.get(uuid).map(Reference::get).orElse(null));

        //end the db life-cycle
        manager.disable();
        Mockito.verify(mockDatabase).load(Mockito.eq(uuid.toString())); // cache not exist so try from db
        // new data no longer saves unless required
    }

    @Test
    public void updateKeyString() throws Exception {
        Map<UUID, TempValue> cachedElements = (Map<UUID, TempValue>) Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = (Map<String, UUID>) Whitebox.getInternalState(manager, "nameToKeyMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.enable();
        manager.load();

        //get (new)
        TempValue value = manager.getOrNew(uuid).map(Reference::get).orElse(null);
        assertEquals(value, cachedElements.get(uuid));

        //update
        value.setStringKey("NewKey");

        //get
        assertEquals(value, manager.get(uuid).map(Reference::get).orElse(null));
        assertEquals(value, manager.get("NewKey").map(Reference::get).orElse(null));

        //update2
        value.setStringKey("NewKey2");

        //get2
        assertEquals(value, manager.get(uuid).map(Reference::get).orElse(null));
        assertEquals(value, manager.get("NewKey2").map(Reference::get).orElse(null));

        //update3
        value.setStringKey("");

        //get3
        assertEquals(value, manager.get(uuid).map(Reference::get).orElse(null));
        assertNull(manager.get("").map(Reference::get).orElse(null));

        //update4
        value.setStringKey(null);

        //get4
        assertEquals(value, manager.get(uuid).map(Reference::get).orElse(null));
        assertNull(manager.get("NewKey2").map(Reference::get).orElse(null));

        //end the db life-cycle
        manager.disable();
        Mockito.verify(mockDatabase).load(Mockito.eq(uuid.toString())); // cache not exist so try from db
        // new data no longer saves unless required
    }

    @Test
    public void delete() throws Exception {
        Map<UUID, TempValue> cachedElements = (Map<UUID, TempValue>) Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = (Map<String, UUID>) Whitebox.getInternalState(manager, "nameToKeyMap");
        Map<UUID, String> keyToNameMap = (Map<UUID, String>) Whitebox.getInternalState(manager, "keyToNameMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.enable();
        manager.load();

        //new
        TempValue mockValue = manager.getOrNew(uuid).map(Reference::get).orElse(null);
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
        Mockito.verify(mockDatabase).load(Mockito.eq(uuid.toString()));
        Mockito.verify(mockDatabase).save(Mockito.eq(uuid.toString()), isNull(String.class));
    }

    @Test
    public void reset() throws Exception {
        Map<UUID, TempValue> cachedElements = (Map<UUID, TempValue>) Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = (Map<String, UUID>) Whitebox.getInternalState(manager, "nameToKeyMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.enable();
        manager.load();

        //new
        TempValue mockValue = manager.getOrNew(uuid).map(Reference::get).orElse(null);
        mockValue.setStr("Initial value");

        Assert.assertEquals(mockValue, cachedElements.get(uuid));

        //change value
        mockValue.setStr("ChangedName");
        Assert.assertEquals("ChangedName", mockValue.str);

        //reset
        manager.reset(mockValue);

        //get current data after reset
        TempValue newMockValue = manager.get(uuid).map(Reference::get).orElse(null);
        Assert.assertNotNull(cachedElements.get(uuid));
        Assert.assertNotNull(newMockValue);

        //end the db life-cycle
        manager.disable();
        //2 for setStr() and 1 for delete()
        Mockito.verify(mockDatabase, Mockito.times(3))
                .save(Mockito.eq(uuid.toString()), Mockito.anyString());
    }

    @Test(expected = RuntimeException.class)
    public void resetInvalidCache() throws Exception {
        Map<UUID, TempValue> cachedElements = (Map<UUID, TempValue>) Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = (Map<String, UUID>) Whitebox.getInternalState(manager, "nameToKeyMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.enable();
        manager.load();

        //new
        TempValue mockValue = manager.getOrNew(uuid).map(Reference::get).orElse(null);
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
        Map<String, String> tempDb = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            UUID uuid = UUID.randomUUID();
            tempDb.put(uuid.toString(), "{\"key\": \"" + uuid + "\", \"str\": \"Value Changed\"}");
        }

        doAnswer(invocation -> {
            String key = (String) invocation.getArguments()[0];
            String value = (String) invocation.getArguments()[1];
            tempDb.put(key, value);
            return null;
        }).when(mockDatabase).save(anyString(), anyString());
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
                            .map(Reference::get)
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
        Map<String, String> tempDb = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            UUID uuid = UUID.randomUUID();
            tempDb.put(uuid.toString(), "{\"key\": \"" + uuid + "\", \"str\": \"Value Changed\"}");
        }

        doAnswer(invocation -> {
            String key = (String) invocation.getArguments()[0];
            String value = (String) invocation.getArguments()[1];
            tempDb.put(key, value);
            return null;
        }).when(mockDatabase).save(anyString(), anyString());
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
                .map(Reference::get)
                .ifPresent(val -> val.setStringKey("Other4")));

        manager.disable();
        thread.join();
        thread2.join();
        thread3.join();
    }

    @Test
    public void forEach() throws Exception {
        //start manager
        manager.enable();
        manager.load();

        //get (new)
        for (int i = 0; i < 100; i++) {
            UUID uuid = UUID.randomUUID();
            TempValue value = manager.getOrNew(uuid).map(Reference::get).orElse(null);
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
        Map<UUID, TempValue> cachedElements = (Map<UUID, TempValue>) Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = (Map<String, UUID>) Whitebox.getInternalState(manager, "nameToKeyMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.enable();
        manager.load();

        //new
        TempValue mockValue = manager.getOrNew(uuid).map(Reference::get).orElse(null);
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
        new ElementCachingManagerModule<>(TempManager.class, TempValue.class);
    }

    @Test(expected = AssertionError.class)
    public void testTypeAssertionFail() {
        new ElementCachingManagerModule<>(TempManager.class, TempValue2.class);
    }

    @Singleton
    static class TempManager extends AbstractManagerElementCaching<UUID, TempValue> {
        @Inject
        public TempManager(PluginMain main, ISerializer serializer, Injector injector) {
            super(main, serializer, injector, TempValue.class);
        }

        @Override
        protected Databases.DatabaseFactory createDatabaseFactory() {
            return (type) -> mockDatabase;
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
            super(null);
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
            this.str = str;

            notifyObservers();
            return this;
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