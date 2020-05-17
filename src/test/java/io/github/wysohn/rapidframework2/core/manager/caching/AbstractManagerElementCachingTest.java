package io.github.wysohn.rapidframework2.core.manager.caching;

import io.github.wysohn.rapidframework2.core.database.Database;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.config.ManagerConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.lang.ref.Reference;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AbstractManagerElementCachingTest {
    class TempValue extends CachedElement<UUID> {
        public Object dummy;
        private String str;

        public TempValue(UUID uuid) {
            super(uuid);
            str = "SomeName";
        }

        public TempValue(UUID uuid, String stringKey){
            super(uuid);
            this.str = stringKey;
        }

        @Override
        public String getStringKey() {
            return str;
        }

        public TempValue setStr(String str) {
            this.str = str;

            notifyObservers();

            return this;
        }
    }

    class TempManager extends AbstractManagerElementCaching<UUID, TempValue> {
        public TempManager(int loadPriority) {
            super(loadPriority);
        }

        @Override
        protected Database.DatabaseFactory<TempValue> createDatabaseFactory() {
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

    private Database<TempValue> mockDatabase;
    private PluginMain mockMain;
    private ManagerConfig mockConfig;
    private Logger mockLogger;
    private TempManager manager;

    @Before
    public void init(){
        mockDatabase = Mockito.mock(Database.class);
        mockMain = Mockito.mock(PluginMain.class);
        mockConfig = Mockito.mock(ManagerConfig.class);
        mockLogger = Mockito.mock(Logger.class);
        manager = new TempManager(0);

        Whitebox.setInternalState(manager, "main", mockMain);

        Mockito.when(mockMain.getLogger()).thenReturn(mockLogger);
        Mockito.when(mockMain.conf()).thenReturn(mockConfig);

        Mockito.when(mockConfig.get(Mockito.anyString())).thenReturn(Optional.of("file"));
    }

    @Test
    public void setConstructionHandle() throws Exception{
        Object someObj = new Object();

        manager.setConstructionHandle(obj -> obj.dummy = someObj);
        UUID uuid = UUID.randomUUID();

        Mockito.when(mockDatabase.load(Mockito.eq(uuid.toString()), Mockito.any())).thenReturn(null);

        //start manager
        manager.enable();
        manager.load();

        TempValue tempValue = manager.getOrNew(uuid).map(Reference::get).orElse(null);
        tempValue.setStr("Constructed");

        Assert.assertEquals(someObj, tempValue.dummy);
    }

    @Test
    public void getCacheSize() throws Exception{
        Mockito.when(mockDatabase.load(Mockito.any(), Mockito.any())).thenReturn(null);

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

        for(UUID uuid : uuids){
            manager.getOrNew(uuid);
        }

        Assert.assertEquals(5, manager.getCacheSize());
    }

    @Test
    public void testLoad() throws Exception{
        UUID[] uuids = new UUID[]{
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
        };
        TempValue[] mockValues = new TempValue[]{
                new TempValue(uuids[0], "value0"),
                new TempValue(uuids[1], "value1"),
                new TempValue(uuids[2], "value2"),
                new TempValue(uuids[3], "value3"),
                new TempValue(uuids[4], "value4"),
        };

        Mockito.when(mockDatabase.getKeys()).thenReturn(Arrays.stream(uuids)
                .map(UUID::toString)
                .collect(Collectors.toSet()));
        Mockito.when(mockDatabase.load(Mockito.anyString(), Mockito.any())).then(invocation -> {
            String key = (String) invocation.getArguments()[0];
            TempValue defVal = (TempValue) invocation.getArguments()[1];

            for(int i = 0; i < uuids.length; i++){
                if(key.equals(uuids[i].toString())){
                    return mockValues[i];
                }
            }

            throw new RuntimeException("Test is not configured correctly.");
        });

        //start manager
        manager.enable();
        manager.load();

        Map<UUID, TempValue> cachedElements = Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = Whitebox.getInternalState(manager, "nameMap");
        List observers = Whitebox.getInternalState(manager, "observers");

        for(int i = 0; i < uuids.length; i++){
            Assert.assertEquals(mockValues[i], cachedElements.get(uuids[i]));
            Assert.assertEquals(uuids[i], nameMap.get("value"+i));

            Mockito.verify(mockDatabase).load(Mockito.eq(uuids[i].toString()), Mockito.isNull(TempValue.class));

            Vector<Observer> obs = Whitebox.getInternalState(mockValues[i], "observers");
            Assert.assertTrue(observers.stream().allMatch(obs::contains));
        }
    }

    @Test
    public void getDecached() throws Exception{
        Map<UUID, TempValue> cachedElements = Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = Whitebox.getInternalState(manager, "nameMap");

        UUID uuid = UUID.randomUUID();
        TempValue value = new TempValue(uuid).setStr("SomeName");

        //start manager
        manager.enable();
        manager.load();

        //get
        Mockito.when(mockDatabase.load(Mockito.eq(uuid.toString()), Mockito.any())).thenReturn(value);
        Assert.assertEquals(value, manager.get(uuid).map(Reference::get).orElse(null));

        //check if cache is updated
        Assert.assertEquals(value, cachedElements.get(uuid));
        Assert.assertEquals(uuid, nameMap.get("SomeName"));

        //end the db life-cycle
        manager.disable();
        Mockito.verify(mockDatabase).load(Mockito.eq(uuid.toString()), Mockito.isNull(TempValue.class));
    }

    @Test
    public void valueNotExist() throws Exception{
        Map<UUID, TempValue> cachedElements = Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = Whitebox.getInternalState(manager, "nameMap");

        UUID uuid = UUID.randomUUID();
        TempValue value = new TempValue(uuid).setStr("SomeName");

        //start manager
        manager.enable();
        manager.load();

        //get
        Assert.assertFalse(manager.get(uuid).isPresent());

        //end the db life-cycle
        manager.disable();
        Mockito.verify(mockDatabase).load(Mockito.eq(uuid.toString()), Mockito.isNull(TempValue.class));
    }

    @Test
    public void getOrNew() throws Exception{
        Map<UUID, TempValue> cachedElements = Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = Whitebox.getInternalState(manager, "nameMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.enable();
        manager.load();

        //get (new)
        TempValue value = manager.getOrNew(uuid).map(Reference::get).orElse(null);
        value.setStr("SomeName");

        Assert.assertEquals(value, cachedElements.get(uuid));
        Assert.assertEquals(uuid, nameMap.get("SomeName"));

        //update
        Assert.assertTrue(manager.setName(uuid, "OtherName"));

        //get
        Mockito.when(mockDatabase.load(Mockito.eq(uuid.toString()), Mockito.any())).thenReturn(value);
        Assert.assertEquals(value, manager.get(uuid).map(Reference::get).orElse(null));
        Assert.assertEquals(value, manager.get("OtherName").map(Reference::get).orElse(null));

        //end the db life-cycle
        manager.disable();
        Mockito.verify(mockDatabase).load(Mockito.eq(uuid.toString()), Mockito.isNull(TempValue.class)); // cache not exist so try from db
        // new data no longer saves unless required
    }

    @Test
    public void delete() throws Exception{
        Map<UUID, TempValue> cachedElements = Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = Whitebox.getInternalState(manager, "nameMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.enable();
        manager.load();

        //new
        TempValue mockValue = manager.getOrNew(uuid).map(Reference::get).orElse(null);
        mockValue.setStr("SomeName");

        Assert.assertEquals(mockValue, cachedElements.get(uuid));
        Assert.assertEquals(uuid, nameMap.get("SomeName"));

        //delete
        manager.delete(uuid);
        Assert.assertNull(cachedElements.get(uuid));
        Assert.assertNull(nameMap.get("SomeName"));

        //end the db life-cycle
        manager.disable();
        Mockito.verify(mockDatabase).getKeys();
        Mockito.verify(mockDatabase).load(Mockito.eq(uuid.toString()), Matchers.isNull(TempValue.class));
        Mockito.verify(mockDatabase).save(Mockito.eq(uuid.toString()), Matchers.isNull(TempValue.class));
    }

    @Test
    public void reset() throws Exception {
        Map<UUID, TempValue> cachedElements = Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = Whitebox.getInternalState(manager, "nameMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.enable();
        manager.load();

        //new
        TempValue mockValue = manager.getOrNew(uuid).map(Reference::get).orElse(null);
        mockValue.setStr("Initial value");

        Assert.assertEquals(mockValue, cachedElements.get(uuid));
        Assert.assertEquals(uuid, nameMap.get("SomeName"));

        //change value
        mockValue.setStr("ChangedName");
        Assert.assertEquals("ChangedName", mockValue.str);

        //reset
        manager.setConstructionHandle((v) -> v.setStr("ConstructionHandleCalled"));
        manager.reset(mockValue);

        //get current data after reset
        TempValue newMockValue = manager.get(uuid).map(Reference::get).orElse(null);
        Assert.assertNotNull(cachedElements.get(uuid));
        Assert.assertNotNull(nameMap.get("SomeName"));
        Assert.assertNotNull(newMockValue);
        Assert.assertEquals("ConstructionHandleCalled", newMockValue.str);

        //end the db life-cycle
        manager.disable();
        //one for new, one for change value
        Mockito.verify(mockDatabase, Mockito.times(2)).save(Mockito.eq(uuid.toString()), Mockito.eq(mockValue));
        //reset will delete data from database
        Mockito.verify(mockDatabase).save(Mockito.eq(uuid.toString()), Mockito.isNull(TempValue.class));
        //reset -> construction handle called -> setStr() is called -> new data saved to db
        Mockito.verify(mockDatabase, Mockito.times(1)).save(Mockito.eq(uuid.toString()), Mockito.eq(newMockValue));
    }

    @Test(expected = RuntimeException.class)
    public void resetInvalidCache() throws Exception {
        Map<UUID, TempValue> cachedElements = Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = Whitebox.getInternalState(manager, "nameMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.enable();
        manager.load();

        //new
        TempValue mockValue = manager.getOrNew(uuid).map(Reference::get).orElse(null);
        mockValue.setStr("Initial value");

        Assert.assertEquals(mockValue, cachedElements.get(uuid));
        Assert.assertEquals(uuid, nameMap.get("SomeName"));

        //change value
        mockValue.setStr("ChangedName");
        Assert.assertEquals("ChangedName", mockValue.str);

        //reset
        manager.setConstructionHandle((v) -> v.setStr("ConstructionHandleCalled"));
        manager.reset(mockValue);
        //test with old instance (the observer should be unregistered at this point)
        //it will throw exception if it's still subscribed with observer
        mockValue.setStr("Invalid call");
    }

    @Test
    public void keySet() {
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
    public void forEachAsync() throws Exception {
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
        manager.forEach(tempValue -> names.add(tempValue.str), true);
        Assert.assertEquals(100, names.size());

        //end the db life-cycle
        manager.disable();
    }

    @Test
    public void search() throws Exception {
        Map<UUID, TempValue> cachedElements = Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = Whitebox.getInternalState(manager, "nameMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.enable();
        manager.load();

        //new
        TempValue mockValue = manager.getOrNew(uuid).map(Reference::get).orElse(null);
        mockValue.setStr("Initial value");

        Assert.assertEquals(mockValue, cachedElements.get(uuid));

        //search
        Assert.assertTrue(manager.search(tempValue -> tempValue.getStringKey().equals("Other")).isEmpty());
        Assert.assertEquals(1, manager.search(tempValue ->
                tempValue.getStringKey().equals("Initial value")).size());
    }
}