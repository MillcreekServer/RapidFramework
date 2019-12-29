package io.github.wysohn.rapidframework2.core.manager.common;

import io.github.wysohn.rapidframework2.core.database.Database;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.manager.NamedElement;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.config.ManagerConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AbstractManagerElementCachingTest {

    class TempValue implements NamedElement{
        private final String str;

        public TempValue(String str) {
            this.str = str;
        }

        @Override
        public String getName() {
            return str;
        }
    }

    class TempManager extends AbstractManagerElementCaching<UUID, TempValue>{
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
    public void testLoad() throws Exception{
        UUID[] uuids = new UUID[]{
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
        };
        TempValue[] mockValues = new TempValue[]{
                new TempValue("value0"),
                new TempValue("value1"),
                new TempValue("value2"),
                new TempValue("value3"),
                new TempValue("value4"),
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

        for(int i = 0; i < uuids.length; i++){
            Assert.assertEquals(mockValues[i], cachedElements.get(uuids[i]));
            Assert.assertEquals(uuids[i], nameMap.get("value"+i));
        }
    }

    @Test
    public void saveAndGet() throws Exception{
        Map<UUID, TempValue> cachedElements = Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = Whitebox.getInternalState(manager, "nameMap");

        UUID uuid = UUID.randomUUID();
        TempValue mockValue = Mockito.mock(TempValue.class);
        Mockito.when(mockValue.getName()).thenReturn("SomeName");

        //start manager
        manager.load();

        //save
        manager.save(uuid, mockValue);
        Assert.assertEquals(mockValue, cachedElements.get(uuid));
        Assert.assertEquals(uuid, nameMap.get("SomeName"));

        //get
        Mockito.when(mockDatabase.load(Mockito.eq(uuid.toString()), Mockito.any())).thenReturn(mockValue);
        Assert.assertEquals(mockValue, manager.get(uuid).orElse(null));

        //end the db life-cycle
        manager.disable();
        Mockito.verify(mockDatabase).save(Mockito.eq(uuid.toString()), Mockito.eq(mockValue));
    }

    @Test
    public void saveAndGetNoName() throws Exception{
        Map<UUID, TempValue> cachedElements = Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = Whitebox.getInternalState(manager, "nameMap");

        UUID uuid = UUID.randomUUID();
        TempValue mockValue = Mockito.mock(TempValue.class);
        Mockito.when(mockValue.getName()).thenReturn(null);

        //start manager
        manager.load();

        //save
        manager.save(uuid, mockValue);
        Assert.assertEquals(mockValue, cachedElements.get(uuid));
        Assert.assertNull(nameMap.get("SomeName"));

        //get
        Mockito.when(mockDatabase.load(Mockito.eq(uuid.toString()), Mockito.any())).thenReturn(mockValue);
        Assert.assertEquals(mockValue, manager.get(uuid).orElse(null));

        //end the db life-cycle
        manager.disable();
        Mockito.verify(mockDatabase).save(Mockito.eq(uuid.toString()), Mockito.eq(mockValue));
    }

    @Test
    public void delete() throws Exception{
        Map<UUID, TempValue> cachedElements = Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = Whitebox.getInternalState(manager, "nameMap");

        UUID uuid = UUID.randomUUID();
        TempValue mockValue = Mockito.mock(TempValue.class);
        Mockito.when(mockValue.getName()).thenReturn("SomeName");

        //start manager
        manager.load();

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