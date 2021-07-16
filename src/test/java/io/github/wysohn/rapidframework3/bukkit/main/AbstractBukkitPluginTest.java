package io.github.wysohn.rapidframework3.bukkit.main;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework3.bukkit.testutils.AbstractBukkitTest;
import io.github.wysohn.rapidframework3.core.command.SubCommand;
import io.github.wysohn.rapidframework3.core.inject.module.LanguagesModule;
import io.github.wysohn.rapidframework3.core.inject.module.ManagerModule;
import io.github.wysohn.rapidframework3.core.main.Manager;
import io.github.wysohn.rapidframework3.core.main.PluginMainBuilder;
import io.github.wysohn.rapidframework3.core.player.AbstractPlayerWrapper;
import io.github.wysohn.rapidframework3.interfaces.io.file.IFileReader;
import io.github.wysohn.rapidframework3.interfaces.io.file.IFileWriter;
import io.github.wysohn.rapidframework3.interfaces.language.ILangSessionFactory;
import io.github.wysohn.rapidframework3.interfaces.message.IBroadcaster;
import io.github.wysohn.rapidframework3.interfaces.plugin.ITaskSupervisor;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework3.interfaces.store.IKeyValueStorage;
import io.github.wysohn.rapidframework3.modules.GuiceDebug;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.mockito.Mockito.mock;

public class AbstractBukkitPluginTest extends AbstractBukkitTest {
    private static Logger mockLogger;

    private static IFileReader mockFileReader;
    private static IFileWriter mockFileWriter;

    private static ILangSessionFactory langSessionFactory;
    private static IBroadcaster broadcaster;

    private static IKeyValueStorage mockStorage;
    private static ITaskSupervisor mockTaskSupervisor;
    private static ISerializer mockSerializer;

    @Before
    public void init() {
        GuiceDebug.enable();

        mockTaskSupervisor = mock(ITaskSupervisor.class);
        mockSerializer = mock(ISerializer.class);

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
            super(mockServer);
        }

        @Override
        protected void init(PluginMainBuilder builder) {
            builder.addModule(new LanguagesModule());
            builder.addModule(new ManagerModule(Manager1.class));
        }

        @Override
        protected void registerCommands(List<SubCommand.Builder> commands) {

        }

        @Override
        protected Optional<? extends AbstractPlayerWrapper> getPlayerWrapper(UUID uuid) {
            return Optional.empty();
        }
    }

    @Singleton
    private static class Manager1 extends Manager {

        @Inject
        public Manager1() {
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
}