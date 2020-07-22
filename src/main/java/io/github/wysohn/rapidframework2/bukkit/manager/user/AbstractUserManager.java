package io.github.wysohn.rapidframework2.bukkit.manager.user;

import io.github.wysohn.rapidframework2.bukkit.main.objects.BukkitPlayer;
import io.github.wysohn.rapidframework2.core.manager.caching.AbstractManagerElementCaching;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.ref.Reference;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractUserManager<V extends BukkitPlayer>
        extends AbstractManagerElementCaching<UUID, V>
        implements Listener {
    public AbstractUserManager(int loadPriority) {
        super(loadPriority);
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
