package io.github.wysohn.rapidframework3.bukkit.manager.user;

import com.google.inject.Injector;
import io.github.wysohn.rapidframework3.bukkit.data.BukkitPlayer;
import io.github.wysohn.rapidframework3.core.caching.AbstractManagerElementCaching;
import io.github.wysohn.rapidframework3.core.main.ManagerConfig;
import io.github.wysohn.rapidframework3.interfaces.plugin.IShutdownHandle;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.lang.ref.Reference;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public abstract class AbstractUserManager<V extends BukkitPlayer>
        extends AbstractManagerElementCaching<UUID, V>
        implements Listener {

    public AbstractUserManager(String pluginName,
                               Logger logger,
                               ManagerConfig config,
                               File pluginDir,
                               IShutdownHandle shutdownHandle,
                               ISerializer serializer,
                               Injector injector,
                               Class<V> type) {
        super(pluginName, logger, config, pluginDir, shutdownHandle, serializer, injector, type);
    }

    @Override
    protected UUID fromString(String string) {
        return UUID.fromString(string);
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if (!this.get(event.getUniqueId()).map(Reference::get).isPresent()) {
            getOrNew(event.getUniqueId());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Optional.of(event.getPlayer())
                .ifPresent(player -> getOrNew(player.getUniqueId())
                        .map(Reference::get)
                        .orElse(null)
                        .setSender(player)
                        .setLastKnownName(player.getName()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

    }
}
