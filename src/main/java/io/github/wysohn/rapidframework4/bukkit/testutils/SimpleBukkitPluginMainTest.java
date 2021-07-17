package io.github.wysohn.rapidframework4.bukkit.testutils;

import io.github.wysohn.rapidframework4.bukkit.main.AbstractBukkitPlugin;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Simple test class to imitate onLoad() and onEnable() being invoked by the Bukkit API.
 * <p>
 * Quite useful to check if plugin loads without problem. However, all this does
 * is calling {@link JavaPlugin#onLoad()} and {@link JavaPlugin#onEnable()}, so
 * it does not fully cover the necessary tests of entire plugin (which would involve
 * Event API and all other complicated stuff).
 *
 * @param <T> The plugin main class to test.
 */
public abstract class SimpleBukkitPluginMainTest<T extends AbstractBukkitPlugin> {
    public abstract T instantiate(Server server);

    public Server enable(Logger logger) {
        Server server = mock(Server.class);

        when(server.getLogger()).thenReturn(logger);

        T plugin = instantiate(server);
        plugin.onLoad();
        plugin.onEnable();

        return server;
    }

    public Server enable() {
        Logger logger = Logger.getGlobal();
        return enable(logger);
    }
}
