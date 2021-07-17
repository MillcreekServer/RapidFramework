package io.github.wysohn.rapidframework4.bukkit.manager.user;

import com.google.inject.Injector;
import io.github.wysohn.rapidframework4.bukkit.data.BukkitPlayer;
import io.github.wysohn.rapidframework4.core.caching.AbstractManagerElementCaching;
import io.github.wysohn.rapidframework4.core.inject.factory.IDatabaseFactoryCreator;
import io.github.wysohn.rapidframework4.core.main.ManagerConfig;
import io.github.wysohn.rapidframework4.interfaces.plugin.IShutdownHandle;
import io.github.wysohn.rapidframework4.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework4.interfaces.serialize.ITypeAsserter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
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
                               ITypeAsserter asserter,
                               IDatabaseFactoryCreator factoryCreator,
                               Injector injector,
                               String tableName,
                               Class<V> type) {
        super(pluginName,
              logger,
              config,
              pluginDir,
              shutdownHandle,
              serializer,
              asserter,
              factoryCreator,
              injector,
              tableName,
              type);
    }

    @Override
    protected UUID fromString(String string) {
        return UUID.fromString(string);
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if (!this.get(event.getUniqueId()).isPresent()) {
            getOrNew(event.getUniqueId());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Optional.of(event.getPlayer())
                .ifPresent(player -> getOrNew(player.getUniqueId())
                        .orElse(null)
                        .setSender(player)
                        .setLastKnownName(player.getName()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

    }
}
