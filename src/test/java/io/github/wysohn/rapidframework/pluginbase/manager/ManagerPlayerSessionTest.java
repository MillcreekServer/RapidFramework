package io.github.wysohn.rapidframework.pluginbase.manager;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.UUID;

public class ManagerPlayerSessionTest extends AbstractTest {

    private ManagerPlayerSession<PluginBase, TempPlayerWrapper> managerPlayerSession;

    @Override
    public void init() {
        super.init();

        managerPlayerSession = new TempManagerPlayerSession(mockBase, 0);
    }

    @Test
    public void onLogin() {
        UUID uuid = UUID.randomUUID();

        TempPlayerWrapper tempPlayerWrapper = managerPlayerSession.get(uuid);
        Assert.assertNull(tempPlayerWrapper);

        AsyncPlayerPreLoginEvent asyncPlayerPreLoginEvent = Mockito.mock(AsyncPlayerPreLoginEvent.class);
        Mockito.when(asyncPlayerPreLoginEvent.getUniqueId()).thenReturn(uuid);
        Mockito.when(asyncPlayerPreLoginEvent.getName()).thenReturn("test");
        managerPlayerSession.onLogin(asyncPlayerPreLoginEvent);

        tempPlayerWrapper = managerPlayerSession.get(uuid);
        Assert.assertNotNull(tempPlayerWrapper);
        Assert.assertEquals(uuid, tempPlayerWrapper.getUuid());
        Assert.assertEquals("test", tempPlayerWrapper.getDisplayName());
    }

    @Test
    public void onJoin() throws Exception {
        UUID uuid = UUID.randomUUID();

        TempPlayerWrapper tempPlayerWrapper = managerPlayerSession.get(uuid);
        Assert.assertNull(tempPlayerWrapper);

        PlayerJoinEvent playerJoinEvent = Mockito.mock(PlayerJoinEvent.class);
        Mockito.when(playerJoinEvent.getPlayer()).thenReturn(mockPlayer);
        managerPlayerSession.onJoin(playerJoinEvent);

        tempPlayerWrapper = managerPlayerSession.get(uuid);
        Assert.assertNotNull(tempPlayerWrapper);
        Assert.assertEquals(uuid, tempPlayerWrapper.getUuid());
        Assert.assertEquals("test", tempPlayerWrapper.getDisplayName());
    }

    static class TempPlayerWrapper extends ManagerPlayerSession.PlayerWrapper {
        public TempPlayerWrapper(UUID uuid) {
            super(uuid);
        }
    }

    static class TempManagerPlayerSession extends ManagerPlayerSession<PluginBase, TempPlayerWrapper> {
        public TempManagerPlayerSession(PluginBase base, int loadPriority) {
            super(base, loadPriority, TempPlayerWrapper.class);
        }

        @Override
        protected TempPlayerWrapper createNewUser(UUID uuid) {
            return new TempPlayerWrapper(uuid);
        }
    }
}