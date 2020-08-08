package io.github.wysohn.rapidframework2.bukkit.manager.user;

import io.github.wysohn.rapidframework2.bukkit.main.objects.BukkitPlayer;
import io.github.wysohn.rapidframework2.bukkit.testutils.AbstractBukkitTest;
import io.github.wysohn.rapidframework2.bukkit.testutils.PluginMainTestBuilder;
import io.github.wysohn.rapidframework2.bukkit.testutils.manager.ManagerTestBuilder;
import io.github.wysohn.rapidframework2.core.database.Database;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractUserManagerTest extends AbstractBukkitTest {

    private Database<User> mockDatabase;
    private Temp manager;
    private PluginMainTestBuilder builder;

    @Before
    public void init() throws IOException {
        mockDatabase = mock(Database.class);
        when(mockDatabase.load(anyString(), any(User.class))).thenReturn(new User(UUID.randomUUID()));

        manager = new Temp(PluginMain.Manager.NORM_PRIORITY);
        builder = PluginMainTestBuilder.create("civ",
                "civ",
                manager);
    }

    @Test
    public void onLogin() throws InvocationTargetException, UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        ManagerTestBuilder.of(builder.getMain(), manager, AsyncPlayerPreLoginEvent.class)
                .before(invokeMethods(PluginMain.Manager::enable, PluginMain.Manager::load))
                .mockEvent((man) -> new AsyncPlayerPreLoginEvent(PLAYER_NAME, address, PLAYER_UUID))
                .expect((man) -> man.get(PLAYER_UUID).isPresent())
                .test();
    }

    @Test
    public void onJoin() throws InvocationTargetException {
        ManagerTestBuilder.of(builder.getMain(), manager, PlayerJoinEvent.class)
                .before(invokeMethods(PluginMain.Manager::enable, PluginMain.Manager::load))
                .mockEvent((man) -> new PlayerJoinEvent(player(), "join"))
                .expect((man) -> man.get(PLAYER_UUID).isPresent())
                .test();
    }

    @Test
    public void onQuit() {

    }

    private static class User extends BukkitPlayer {

        protected User(UUID key) {
            super(key);
        }
    }

    private class Temp extends AbstractUserManager<User> {

        public Temp(int loadPriority) {
            super(loadPriority);
        }

        @Override
        protected Database.DatabaseFactory<User> createDatabaseFactory() {
            return (name) -> mockDatabase;
        }

        @Override
        protected User newInstance(UUID key) {
            return new User(key);
        }
    }
}