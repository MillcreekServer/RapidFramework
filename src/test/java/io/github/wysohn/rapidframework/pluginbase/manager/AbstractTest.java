package io.github.wysohn.rapidframework.pluginbase.manager;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Logger;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PluginBase.class, Bukkit.class})
public abstract class AbstractTest {
    protected PluginBase mockBase;
    protected Player mockPlayer;
    protected UUID mockPlayerUuid = UUID.randomUUID();

    @Before
    public void init() {
        mockBase = PowerMockito.mock(PluginBase.class);
        mockPlayer = Mockito.mock(Player.class);
        Mockito.when(mockPlayer.getUniqueId()).thenReturn(mockPlayerUuid);
        Mockito.when(mockPlayer.getName()).thenReturn("test");

        PowerMockito.mockStatic(Bukkit.class);
        Mockito.when(Bukkit.getOfflinePlayer(Mockito.any(UUID.class))).thenReturn(mockPlayer);

        PowerMockito.when(mockBase.getDataFolder()).thenReturn(Paths.get("bin", "testPluginFolder").toFile());
        PowerMockito.when(mockBase.getLogger()).thenReturn(Mockito.mock(Logger.class));
    }
}
