package io.github.wysohn.rapidframework3.bukkit.manager.user;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.github.wysohn.rapidframework3.bukkit.data.BukkitPlayer;
import io.github.wysohn.rapidframework3.bukkit.testutils.manager.AbstractBukkitManagerTest;
import io.github.wysohn.rapidframework3.core.database.Database;
import io.github.wysohn.rapidframework3.core.database.Databases;
import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework3.modules.MockMainModule;
import io.github.wysohn.rapidframework3.modules.MockSerializerModule;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractUserManagerTest extends AbstractBukkitManagerTest {
    private static Database mockDatabase;

    private ISerializer mockSerializer;

    private List<Module> moduleList = new LinkedList<>();

    @Before
    public void init() throws IOException {
        mockDatabase = mock(Database.class);
        mockSerializer = mock(ISerializer.class);

        when(mockDatabase.load(anyString())).thenReturn("{\"key\": \"" + PLAYER_UUID + "\"}");

        moduleList.add(new MockMainModule());
        moduleList.add(new MockSerializerModule(mockSerializer));
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

        protected User(UUID key) {
            super(key);
        }
    }

    @Singleton
    private static class Temp extends AbstractUserManager<User> {
        @Inject
        public Temp(PluginMain main, ISerializer serializer, Injector injector) {
            super(main, serializer, injector, User.class);
        }

        @Override
        protected Databases.DatabaseFactory createDatabaseFactory() {
            return (name) -> mockDatabase;
        }

        @Override
        protected User newInstance(UUID key) {
            return new User(key);
        }
    }
}