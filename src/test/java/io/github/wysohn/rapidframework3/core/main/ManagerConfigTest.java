package io.github.wysohn.rapidframework3.core.main;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import io.github.wysohn.rapidframework3.core.interfaces.io.file.IFileReader;
import io.github.wysohn.rapidframework3.core.interfaces.io.file.IFileWriter;
import io.github.wysohn.rapidframework3.core.interfaces.serialize.IStorageSerializer;
import io.github.wysohn.rapidframework3.core.interfaces.store.temporary.IKeyValueStorage;
import io.github.wysohn.rapidframework3.modules.MockFileIOModule;
import io.github.wysohn.rapidframework3.modules.MockMainModule;
import io.github.wysohn.rapidframework3.modules.MockPluginDirectoryModule;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ManagerConfigTest {
    private List<Module> moduleList = new LinkedList<>();
    private IStorageSerializer mockSerializer;
    private IKeyValueStorage mockStorage;
    private IFileReader mockFileReader;
    private IFileWriter mockFileWriter;

    @Before
    public void init() {
        mockFileReader = mock(IFileReader.class);
        mockFileWriter = mock(IFileWriter.class);
        mockSerializer = mock(IStorageSerializer.class);
        mockStorage = mock(IKeyValueStorage.class);

        moduleList.add(new MockMainModule());
        moduleList.add(new MockPluginDirectoryModule());
        moduleList.add(new MockFileIOModule(mockFileReader, mockFileWriter));
        moduleList.add(new AbstractModule() {

        });
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

        when(mockSerializer.serializeToString(any())).thenReturn("This is serialized!");

        Object value = new Object();

        managerConfig.put("some.key", value);
        verify(mockStorage).put("some.key", value);

        managerConfig.disable();
        verify(mockFileWriter).accept(any(File.class), eq("This is serialized!"));
    }

    @Test
    public void getKeys() {
        ManagerConfig managerConfig = Guice.createInjector(moduleList)
                .getInstance(ManagerConfig.class);

        Set keys = new HashSet();
        when(mockStorage.getKeys(anyBoolean())).thenReturn(keys);

        assertEquals(keys, managerConfig.getKeys(false));
    }

    @Test
    public void restoreFromString() {
        ManagerConfig managerConfig = Guice.createInjector(moduleList)
                .getInstance(ManagerConfig.class);

        String someStr = "Some str";
        managerConfig.restoreFromString(someStr);

        verify(mockStorage).restoreFromString(eq(someStr));
    }

    @Test
    public void storeAsString() {
        ManagerConfig managerConfig = Guice.createInjector(moduleList)
                .getInstance(ManagerConfig.class);

        when(mockSerializer.serializeToString(any())).thenReturn("Store");

        assertEquals("Store", managerConfig.storeAsString());
    }
}