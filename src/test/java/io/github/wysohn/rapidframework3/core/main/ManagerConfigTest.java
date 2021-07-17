package io.github.wysohn.rapidframework3.core.main;

import com.google.inject.Guice;
import com.google.inject.Module;
import io.github.wysohn.rapidframework4.core.main.ManagerConfig;
import io.github.wysohn.rapidframework4.interfaces.store.IKeyValueStorage;
import io.github.wysohn.rapidframework4.testmodules.MockPluginDirectoryModule;
import io.github.wysohn.rapidframework4.testmodules.MockStorageFactoryModule;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ManagerConfigTest {
    private List<Module> moduleList = new LinkedList<>();
    private IKeyValueStorage mockStorage;

    @Before
    public void init() {
        mockStorage = mock(IKeyValueStorage.class);

        moduleList.add(new MockPluginDirectoryModule());
        moduleList.add(new MockStorageFactoryModule(mockStorage));
    }


    @Test
    public void get() {
        ManagerConfig managerConfig = Guice.createInjector(moduleList)
                .getInstance(ManagerConfig.class);

        Object value = new Object();

        when(mockStorage.get(anyString())).thenReturn(Optional.of(value));
        assertEquals(value, managerConfig.get("some.key").orElse(null));
    }

    @Test
    public void put() throws Exception {
        ManagerConfig managerConfig = Guice.createInjector(moduleList)
                .getInstance(ManagerConfig.class);

        Object value = new Object();

        managerConfig.put("some.key", value);
        verify(mockStorage).put("some.key", value);
    }

    @Test
    public void getKeys() {
        ManagerConfig managerConfig = Guice.createInjector(moduleList)
                .getInstance(ManagerConfig.class);

        Set keys = new HashSet();
        when(mockStorage.getKeys(anyBoolean())).thenReturn(keys);

        assertEquals(keys, managerConfig.getKeys(false));
    }
}