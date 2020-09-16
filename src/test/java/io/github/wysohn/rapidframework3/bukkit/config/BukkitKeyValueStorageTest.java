package io.github.wysohn.rapidframework3.bukkit.config;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import io.github.wysohn.rapidframework3.core.inject.factory.IStorageFactory;
import io.github.wysohn.rapidframework3.interfaces.io.file.IFileReader;
import io.github.wysohn.rapidframework3.interfaces.io.file.IFileWriter;
import io.github.wysohn.rapidframework3.interfaces.store.IKeyValueStorage;
import io.github.wysohn.rapidframework3.testmodules.MockFileIOModule;
import io.github.wysohn.rapidframework3.testmodules.MockMainModule;
import io.github.wysohn.rapidframework3.utils.FileUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class BukkitKeyValueStorageTest {

    private IFileReader mockReader;
    private IFileWriter mockWriter;

    private List<Module> moduleList = new LinkedList<>();

    @Inject
    private IStorageFactory storageFactory;

    @Before
    public void init() {
        mockReader = mock(IFileReader.class);
        mockWriter = mock(IFileWriter.class);

        moduleList.add(new MockMainModule());
        moduleList.add(new MockFileIOModule(mockReader, mockWriter));
        moduleList.add(new AbstractModule() {
            @Override
            protected void configure() {
                install(new FactoryModuleBuilder()
                        .implement(IKeyValueStorage.class, BukkitKeyValueStorage.class)
                        .build(IStorageFactory.class));
            }
        });
    }

    @Test
    public void get() throws IOException {
        File mockDirectory = new File("build/tmp/tests/");
        String name = "test.yml";

        String fileContent = "some:\n" +
                "    random:\n" +
                "        key: \"this is value\"\n";
        FileUtil.writeToFile(FileUtil.join(mockDirectory, name), fileContent);

        Guice.createInjector(moduleList).injectMembers(this);
        IKeyValueStorage storage = storageFactory.create(mockDirectory, name);

        assertEquals("this is value", storage.get("some.random.key").orElse(null));
    }

    @Test
    public void put() throws IOException {
        File mockDirectory = new File("build/tmp/tests/");
        String name = "test2.yml";

        Guice.createInjector(moduleList).injectMembers(this);
        IKeyValueStorage storage = storageFactory.create(mockDirectory, name);

        storage.put("some.other.key", "other value 1212323");
        storage.shutdown();

        verify(mockWriter, times(1)).accept(any(), contains("other value 1212323"));
    }

    @Test
    public void getKeys() throws IOException {
        File mockDirectory = new File("build/tmp/tests/");
        String name = "test2.yml";

        String fileContent = "some:\n" +
                "    random:\n" +
                "        key: \"this is value\"\n" +
                "value2: 123\n" +
                "value3: false";
        FileUtil.writeToFile(FileUtil.join(mockDirectory, name), fileContent);

        Guice.createInjector(moduleList).injectMembers(this);
        IKeyValueStorage storage = storageFactory.create(mockDirectory, name);

        Set<String> keys = new HashSet<>();
        keys.add("some");
        keys.add("value2");
        keys.add("value3");
        assertEquals(keys, storage.getKeys(false));
    }
}