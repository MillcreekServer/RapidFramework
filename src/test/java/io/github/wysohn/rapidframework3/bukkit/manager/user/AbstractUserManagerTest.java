package io.github.wysohn.rapidframework3.bukkit.manager.user;

import com.google.inject.*;
import io.github.wysohn.rapidframework3.bukkit.data.BukkitPlayer;
import io.github.wysohn.rapidframework3.bukkit.testutils.manager.AbstractBukkitManagerTest;
import io.github.wysohn.rapidframework3.core.database.Database;
import io.github.wysohn.rapidframework3.core.database.Databases;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginLogger;
import io.github.wysohn.rapidframework3.core.inject.module.PluginInfoModule;
import io.github.wysohn.rapidframework3.core.inject.module.TypeAsserterModule;
import io.github.wysohn.rapidframework3.core.main.ManagerConfig;
import io.github.wysohn.rapidframework3.interfaces.plugin.IShutdownHandle;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework3.interfaces.serialize.ITypeAsserter;
import io.github.wysohn.rapidframework3.testmodules.*;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractUserManagerTest extends AbstractBukkitManagerTest {
    private static Database<User> mockDatabase;

    private ISerializer mockSerializer;

    private List<Module> moduleList = new LinkedList<>();

    @Before
    public void init() throws IOException {
        mockDatabase = mock(Database.class);
        User user = mock(User.class);
        when(user.getKey()).thenReturn(PLAYER_UUID);

        when(mockDatabase.load(anyString())).thenReturn(user);

        moduleList.add(new PluginInfoModule("test", "test", "test"));
        moduleList.add(new MockLoggerModule());
        moduleList.add(new MockConfigModule());
        moduleList.add(new MockPluginDirectoryModule());
        moduleList.add(new MockShutdownModule(() -> {
        }));
        moduleList.add(new MockSerializerModule(mockSerializer));
        moduleList.add(new TypeAsserterModule());
    }

    @Test
    public void onLogin() throws Exception {
        Temp manager = Guice.createInjector(moduleList).getInstance(Temp.class);
        manager.enable();

        mockEvent(manager, login());
        assertTrue(manager.get(PLAYER_UUID).isPresent());
    }

    @Test
    public void onJoin() throws Exception {
        Temp manager = Guice.createInjector(moduleList).getInstance(Temp.class);
        manager.enable();

        mockEvent(manager, join(player()));
        assertTrue(manager.get(PLAYER_UUID).isPresent());
    }

    @Test
    public void onQuit() {

    }

    private static class User extends BukkitPlayer {
        public User() {
            super(null);
        }

        protected User(UUID key) {
            super(key);
        }
    }

    @Singleton
    private static class Temp extends AbstractUserManager<User> {
        @Inject
        public Temp(@Named("pluginName") String pluginName,
                    @PluginLogger Logger logger,
                    ManagerConfig config,
                    @PluginDirectory File pluginDir,
                    IShutdownHandle shutdownHandle,
                    ISerializer serializer,
                    ITypeAsserter asserter,
                    Injector injector) {
            super(pluginName, logger, config, pluginDir, shutdownHandle, serializer, asserter, injector, User.class);
        }

        @Override
        protected Databases.DatabaseFactory<User> createDatabaseFactory() {
            return (clazz, db, others) -> mockDatabase;
        }

        @Override
        protected User newInstance(UUID key) {
            return new User(key);
        }
    }
}