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

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AbstractManagerElementCachingTest {

    class TempValue extends CachedElement<UUID> {
        public Object dummy;
        private String str;

        public TempValue(UUID uuid) {
            super(uuid);
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
        manager.load();

        TempValue tempValue = manager.getOrNew(uuid);
        tempValue.setStr("Constructed");

        Assert.assertEquals(someObj, tempValue.dummy);
    }

    @Test
    public void getCacheSize() throws Exception{
        Mockito.when(mockDatabase.load(Mockito.any(), Mockito.any())).thenReturn(null);

        //start manager
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
                new TempValue(uuids[0]).setStr("value0"),
                new TempValue(uuids[1]).setStr("value1"),
                new TempValue(uuids[2]).setStr("value2"),
                new TempValue(uuids[3]).setStr("value3"),
                new TempValue(uuids[4]).setStr("value4"),
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
        manager.load();

        Map<UUID, TempValue> cachedElements = Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = Whitebox.getInternalState(manager, "nameMap");
        Object observer = Whitebox.getInternalState(manager, "observer");

        for(int i = 0; i < uuids.length; i++){
            Assert.assertEquals(mockValues[i], cachedElements.get(uuids[i]));
            Assert.assertEquals(uuids[i], nameMap.get("value"+i));

            Mockito.verify(mockDatabase).load(Mockito.eq(uuids[i].toString()), Mockito.isNull(TempValue.class));

            Vector<Observer> obs = Whitebox.getInternalState(mockValues[i], "observers");
            Assert.assertTrue(obs.contains(observer));
        }
    }

    @Test
    public void getDecached() throws Exception{
        Map<UUID, TempValue> cachedElements = Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = Whitebox.getInternalState(manager, "nameMap");

        UUID uuid = UUID.randomUUID();
        TempValue value = new TempValue(uuid).setStr("SomeName");

        //start manager
        manager.load();

        //get
        Mockito.when(mockDatabase.load(Mockito.eq(uuid.toString()), Mockito.any())).thenReturn(value);
        Assert.assertEquals(value, manager.get(uuid).orElse(null));

        //check if cache is updated
        Assert.assertEquals(value, cachedElements.get(uuid));
        Assert.assertEquals(uuid, nameMap.get("SomeName"));

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
        manager.load();

        //get (new)
        TempValue value = manager.getOrNew(uuid);
        value.setStr("SomeName");

        Assert.assertEquals(value, cachedElements.get(uuid));
        Assert.assertEquals(uuid, nameMap.get("SomeName"));

        //update
        value.setStr("OtherName");

        //get
        Mockito.when(mockDatabase.load(Mockito.eq(uuid.toString()), Mockito.any())).thenReturn(value);
        Assert.assertEquals(value, manager.get(uuid).orElse(null));
        Assert.assertEquals("OtherName", manager.get(uuid).orElse(null).str);

        //end the db life-cycle
        manager.disable();
        Mockito.verify(mockDatabase, Mockito.times(2)).save(Mockito.eq(uuid.toString()), Mockito.eq(value));
    }

    @Test
    public void delete() throws Exception{
        Map<UUID, TempValue> cachedElements = Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = Whitebox.getInternalState(manager, "nameMap");

        UUID uuid = UUID.randomUUID();

        //start manager
        manager.load();

        //new
        TempValue mockValue = manager.getOrNew(uuid);
        mockValue.setStr("SomeName");

        Assert.assertEquals(mockValue, cachedElements.get(uuid));
        Assert.assertEquals(uuid, nameMap.get("SomeName"));

        //delete
        manager.delete(uuid);
        Assert.assertNull(cachedElements.get(uuid));
        Assert.assertNull(nameMap.get("SomeName"));

        //end the db life-cycle
        manager.disable();
        Mockito.verify(mockDatabase).save(Mockito.eq(uuid.toString()), Matchers.isNull(TempValue.class));
    }

    @Test
    public void keySet() {
    }
}