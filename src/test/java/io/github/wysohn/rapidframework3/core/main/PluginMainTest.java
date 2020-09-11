package io.github.wysohn.rapidframework3.core.main;

import com.google.inject.*;
import io.github.wysohn.rapidframework3.core.inject.module.*;
import io.github.wysohn.rapidframework3.core.interfaces.io.file.IFileReader;
import io.github.wysohn.rapidframework3.core.interfaces.io.file.IFileWriter;
import io.github.wysohn.rapidframework3.core.interfaces.language.ILangSessionFactory;
import io.github.wysohn.rapidframework3.core.interfaces.message.IBroadcaster;
import io.github.wysohn.rapidframework3.core.interfaces.plugn.ITaskSupervisor;
import io.github.wysohn.rapidframework3.core.interfaces.serialize.IStorageSerializer;
import io.github.wysohn.rapidframework3.core.interfaces.store.temporary.IKeyValueStorage;
import io.github.wysohn.rapidframework3.modules.MockFileIOModule;
import io.github.wysohn.rapidframework3.modules.MockLanguageModule;
import io.github.wysohn.rapidframework3.modules.MockPluginConfigModule;
import io.github.wysohn.rapidframework3.modules.MockPluginDirectoryModule;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.mock;

public class PluginMainTest {

    private List<Module> moduleList = new LinkedList<>();

    private IFileReader mockFileReader;
    private IFileWriter mockFileWriter;

    private ILangSessionFactory langSessionFactory;
    private IBroadcaster broadcaster;

    private IStorageSerializer mockSerializer;
    private IKeyValueStorage mockStorage;

    @Before
    public void init() {
        ITaskSupervisor mockTaskSupervisor = mock(ITaskSupervisor.class);

        mockFileReader = mock(IFileReader.class);
        mockFileWriter = mock(IFileWriter.class);

        langSessionFactory = mock(ILangSessionFactory.class);
        broadcaster = mock(IBroadcaster.class);

        mockSerializer = mock(IStorageSerializer.class);
        mockStorage = mock(IKeyValueStorage.class);

        moduleList.add(new PluginInfoModule("testPlugin", "desc", "root"));
        moduleList.add(new DefaultManagersModule());
        moduleList.add(new ManagerModule(Manager1.class,
                Manager2.class,
                Manager3.class));
        moduleList.add(new MediatorModule());
        moduleList.add(new LanguagesModule());
        moduleList.add(new MockPluginDirectoryModule());
        moduleList.add(new MockFileIOModule(mockFileReader, mockFileWriter));
        moduleList.add(new MockLanguageModule(langSessionFactory, broadcaster));
        moduleList.add(new MockPluginConfigModule(mockSerializer, mockStorage));
        moduleList.add(new AbstractModule() {
            @Provides
            ITaskSupervisor getTaskSupervisor() {
                return mockTaskSupervisor;
            }
        });
    }

    @Test
    public void preload() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        PluginMain pluginMain = injector.getInstance(PluginMain.class);

        pluginMain.preload();
    }

    @Test
    public void enable() {
    }

    @Test
    public void load() {
    }

    @Test
    public void disable() {
    }

    private static class Manager1 extends Manager {

        @Inject
        public Manager1(PluginMain main) {
            super(main);
            dependsOn(Manager2.class);
            dependsOn(Manager3.class);
        }

        @Override
        public void enable() throws Exception {

        }

        @Override
        public void load() throws Exception {

        }

        @Override
        public void disable() throws Exception {

        }
    }

    private static class Manager2 extends Manager {

        @Inject
        public Manager2(PluginMain main) {
            super(main);
        }

        @Override
        public void enable() throws Exception {

        }

        @Override
        public void load() throws Exception {

        }

        @Override
        public void disable() throws Exception {

        }
    }

    private static class Manager3 extends Manager {

        @Inject
        public Manager3(PluginMain main) {
            super(main);
            dependsOn(Manager2.class);
        }

        @Override
        public void enable() throws Exception {

        }

        @Override
        public void load() throws Exception {

        }

        @Override
        public void disable() throws Exception {

        }
    }
}