package io.github.wysohn.rapidframework3.bukkit.manager.user;

import com.google.inject.*;
import io.github.wysohn.rapidframework3.bukkit.data.BukkitPlayer;
import io.github.wysohn.rapidframework3.bukkit.testutils.manager.AbstractBukkitManagerTest;
import io.github.wysohn.rapidframework3.core.caching.CachedElement;
import io.github.wysohn.rapidframework3.core.database.Database;
import io.github.wysohn.rapidframework3.core.database.IDatabase;
import io.github.wysohn.rapidframework3.core.database.IDatabaseFactory;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginLogger;
import io.github.wysohn.rapidframework3.core.inject.factory.IDatabaseFactoryCreator;
import io.github.wysohn.rapidframework3.core.inject.module.GsonSerializerModule;
import io.github.wysohn.rapidframework3.core.inject.module.PluginInfoModule;
import io.github.wysohn.rapidframework3.core.inject.module.TypeAsserterModule;
import io.github.wysohn.rapidframework3.core.main.ManagerConfig;
import io.github.wysohn.rapidframework3.interfaces.plugin.IShutdownHandle;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework3.interfaces.serialize.ITypeAsserter;
import io.github.wysohn.rapidframework3.testmodules.MockConfigModule;
import io.github.wysohn.rapidframework3.testmodules.MockLoggerModule;
import io.github.wysohn.rapidframework3.testmodules.MockPluginDirectoryModule;
import io.github.wysohn.rapidframework3.testmodules.MockShutdownModule;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractUserManagerTest extends AbstractBukkitManagerTest {
    private static IDatabase mockDatabase;

    private ISerializer mockSerializer;

    private List<Module> moduleList = new LinkedList<>();

    @Before
    public void init() throws IOException {
        mockDatabase = mock(Database.class);
        mockSerializer = mock(ISerializer.class);

        User user = mock(User.class);
        when(user.getKey()).thenReturn(PLAYER_UUID);

        when(mockDatabase.load(anyString())).thenReturn(user);

        moduleList.add(new PluginInfoModule("test", "test", "test"));
        moduleList.add(new MockLoggerModule());
        moduleList.add(new MockConfigModule());
        moduleList.add(new MockPluginDirectoryModule());
        moduleList.add(new MockShutdownModule(() -> {
        }));
        moduleList.add(new GsonSerializerModule());
        moduleList.add(new TypeAsserterModule());
        moduleList.add(new AbstractModule() {
            @Provides
            public IDatabaseFactoryCreator creator(){
                return typeName -> new IDatabaseFactory() {
                    @Override
                    public <K, V extends CachedElement<K>> IDatabase<K, V> create(String tableName,
                                                                                  Class<V> type,
                                                                                  Function<String, K> fn) {
                        return mockDatabase;
                    }
                };
            }
        });
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

        private User(User copy){
            super(copy.getKey());
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
                    IDatabaseFactoryCreator factoryCreator,
                    Injector injector) {
            super(pluginName,
                  logger,
                  config,
                  pluginDir,
                  shutdownHandle,
                  serializer,
                  asserter,
                  factoryCreator,
                  injector,
                  "User",
                  User.class);
        }

        @Override
        protected User newInstance(UUID key) {
            return new User(key);
        }
    }
}