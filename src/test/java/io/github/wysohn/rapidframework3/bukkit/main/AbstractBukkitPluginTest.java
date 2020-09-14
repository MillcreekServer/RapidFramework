package io.github.wysohn.rapidframework3.bukkit.main;

import io.github.wysohn.rapidframework3.core.command.SubCommand;
import io.github.wysohn.rapidframework3.core.inject.module.LanguagesModule;
import io.github.wysohn.rapidframework3.core.inject.module.ManagerModule;
import io.github.wysohn.rapidframework3.core.main.Manager;
import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.core.main.PluginMainBuilder;
import io.github.wysohn.rapidframework3.core.player.AbstractPlayerWrapper;
import io.github.wysohn.rapidframework3.interfaces.io.file.IFileReader;
import io.github.wysohn.rapidframework3.interfaces.io.file.IFileWriter;
import io.github.wysohn.rapidframework3.interfaces.language.ILangSessionFactory;
import io.github.wysohn.rapidframework3.interfaces.message.IBroadcaster;
import io.github.wysohn.rapidframework3.interfaces.plugin.ITaskSupervisor;
import io.github.wysohn.rapidframework3.interfaces.store.IKeyValueStorage;
import io.github.wysohn.rapidframework3.modules.GuiceDebug;
import io.github.wysohn.rapidframework3.modules.MockGlobalPluginManager;
import io.github.wysohn.rapidframework3.modules.MockPluginDirectoryModule;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractBukkitPluginTest {
    private static Server mockServer;
    private static Logger mockLogger;

    private static IFileReader mockFileReader;
    private static IFileWriter mockFileWriter;

    private static ILangSessionFactory langSessionFactory;
    private static IBroadcaster broadcaster;

    private static IKeyValueStorage mockStorage;
    private static ITaskSupervisor mockTaskSupervisor;

    @Before
    public void init() throws IllegalAccessException, NoSuchFieldException {
        GuiceDebug.enable();

        mockServer = mock(Server.class);
        mockLogger = mock(Logger.class);
        when(mockServer.getLogger()).thenReturn(mockLogger);

        Field serverField = Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(null, mockServer);

        mockTaskSupervisor = mock(ITaskSupervisor.class);

        mockFileReader = mock(IFileReader.class);
        mockFileWriter = mock(IFileWriter.class);

        langSessionFactory = mock(ILangSessionFactory.class);
        broadcaster = mock(IBroadcaster.class);

        mockStorage = mock(IKeyValueStorage.class);
    }

    @Test
    public void onLoad() {
        TempPlugin plugin = new TempPlugin();
        plugin.onLoad();
    }

    @Test
    public void onEnable() {
        TempPlugin plugin = new TempPlugin();
        plugin.onLoad();
        plugin.onEnable();
    }

    @Test
    public void onDisable() {
        TempPlugin plugin = new TempPlugin();
        plugin.onLoad();
        plugin.onEnable();
        plugin.onDisable();
    }

    public static class TempPlugin extends AbstractBukkitPlugin {
        public TempPlugin() {
            super(new JavaPluginLoader(mockServer),
                    new PluginDescriptionFile("test", "test", "test"),
                    new File("build/tmp/tests/"),
                    new File("build/tmp/tests/other"));
        }

        @Override
        protected void init(PluginMainBuilder builder) {
//            builder.addModule(new PluginInfoModule("testPlugin", "desc", "root"));
//            builder.addModule(new DefaultManagersModule());
            builder.addModule(new LanguagesModule(fn -> {

            }));
            builder.addModule(new ManagerModule(Manager1.class));
//            builder.addModule(new MainCommandsModule("test"));
//            builder.addModule(new PlatformModule(new Object()));
//            builder.addModule(new TaskSupervisorModule(mockTaskSupervisor));
            builder.addModule(new MockPluginDirectoryModule());
//            builder.addModule(new MockMessageSenderModule());
//            builder.addModule(new MockFileIOModule(mockFileReader, mockFileWriter));
            builder.addModule(new MockGlobalPluginManager());
//            builder.addModule(new MockStorageFactoryModule(mockStorage));
        }

        @Override
        protected void registerCommands(List<SubCommand> commands) {

        }

        @Override
        protected Optional<? extends AbstractPlayerWrapper> getPlayerWrapper(UUID uuid) {
            return Optional.empty();
        }
    }

    @Singleton
    private static class Manager1 extends Manager {

        @Inject
        public Manager1(PluginMain main) {
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
}