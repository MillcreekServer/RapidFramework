package io.github.wysohn.rapidframework3.core.main;

import com.google.inject.*;
import io.github.wysohn.rapidframework3.core.api.ExternalAPI;
import io.github.wysohn.rapidframework3.core.inject.module.*;
import io.github.wysohn.rapidframework3.interfaces.io.file.IFileReader;
import io.github.wysohn.rapidframework3.interfaces.io.file.IFileWriter;
import io.github.wysohn.rapidframework3.interfaces.language.ILang;
import io.github.wysohn.rapidframework3.interfaces.language.ILangSessionFactory;
import io.github.wysohn.rapidframework3.interfaces.message.IBroadcaster;
import io.github.wysohn.rapidframework3.interfaces.plugin.ITaskSupervisor;
import io.github.wysohn.rapidframework3.interfaces.store.IKeyValueStorage;
import io.github.wysohn.rapidframework3.modules.GuiceDebug;
import io.github.wysohn.rapidframework3.testmodules.*;
import io.github.wysohn.rapidframework3.utils.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.mock;

public class PluginMainTest {

    private List<Module> moduleList = new LinkedList<>();

    private IFileReader mockFileReader;
    private IFileWriter mockFileWriter;

    private ILangSessionFactory langSessionFactory;
    private IBroadcaster broadcaster;

    private IKeyValueStorage mockStorage;
    private ExecutorService executorService;

    @Before
    public void init() {
        GuiceDebug.enable();

        ITaskSupervisor mockTaskSupervisor = mock(ITaskSupervisor.class);

        mockFileReader = mock(IFileReader.class);
        mockFileWriter = mock(IFileWriter.class);

        langSessionFactory = mock(ILangSessionFactory.class);
        broadcaster = mock(IBroadcaster.class);

        mockStorage = mock(IKeyValueStorage.class);

        moduleList.add(new PluginInfoModule("testPlugin", "desc", "root"));
        moduleList.add(new DefaultManagersModule());
        moduleList.add(new ExecutorServiceModule());
        moduleList.add(new ManagerModule(Manager1.class,
                Manager2.class,
                Manager3.class));
        moduleList.add(new MediatorModule());
        moduleList.add(new LanguagesModule());
        moduleList.add(new ExternalAPIModule(Pair.of("SomeOtherPlugin", SomeExternalAPISupport.class)));
        moduleList.add(new MainCommandsModule("test"));
        moduleList.add(new PlatformModule(new Object()));
        moduleList.add(new TaskSupervisorModule(mockTaskSupervisor));
        moduleList.add(new MockPluginDirectoryModule());
        moduleList.add(new MockMessageSenderModule());
        moduleList.add(new MockFileIOModule(mockFileReader, mockFileWriter));
        moduleList.add(new MockGlobalPluginManager());
        moduleList.add(new MockStorageFactoryModule(mockStorage));
        moduleList.add(new MockBroadcasterModule());
        moduleList.add(new MockShutdownModule(() -> {
        }));
        moduleList.add(new MockLoggerModule());
        moduleList.add(new MockDebugStateHandleModule());
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

    private enum TempLangs implements ILang {
        ;

        @Override
        public String[] getEngDefault() {
            return new String[0];
        }
    }

    @Singleton
    private static class Manager1 extends Manager {

        @Inject
        public Manager1() {
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

    @Singleton
    private static class Manager2 extends Manager {

        @Inject
        public Manager2() {
            super();
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

    @Singleton
    private static class Manager3 extends Manager {

        @Inject
        public Manager3() {
            super();
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

    private static class SomeExternalAPISupport extends ExternalAPI {
        public SomeExternalAPISupport(PluginMain main, String pluginName) {
            super(main, pluginName);
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