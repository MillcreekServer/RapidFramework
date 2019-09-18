package io.github.wysohn.rapidframework.pluginbase.manager;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.objects.PluginEntity;
import io.github.wysohn.rapidframework.pluginbase.objects.permissions.PermissionHolder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public abstract class ManagerPlayerSession<PB extends PluginBase, U extends ManagerPlayerSession.PlayerWrapper>
        extends ManagerElementCaching<PB, UUID, U> implements Listener {

    public ManagerPlayerSession(PB base, int loadPriority, Class<U> type) {
        super(base, loadPriority, createDatabaseFactory(base, "Users", type));
    }

    @Override
    protected UUID createKeyFromString(String str) {
        return UUID.fromString(str);
    }

    /**
     * This call is asynchronous. Create new user instance and return it. Though, in
     * some rare cases, it might be called from server thread.
     *
     * @param uuid
     * @return
     */
    protected abstract U createNewUser(UUID uuid);

    @Override
    protected CacheUpdateHandle<UUID, U> getUpdateHandle() {
        return (key, original) -> {
            original.player = Bukkit.getOfflinePlayer(key);
            if (original.player.isOnline())
                original.player = Bukkit.getPlayer(key);
            return original;
        };
    }

    @Override
    protected CacheDeleteHandle<UUID, U> getDeleteHandle() {
        return null;
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent ev) {
        U user = this.get(ev.getUniqueId());
        if (user == null) {
            user = createNewUser(ev.getUniqueId());
        }
        user.displayName = ev.getName();
        this.save(ev.getUniqueId(), user);
    }

    /**
     * If anything has to be done with user instance after it is loaded, override it.
     * The method will be invoked right before it is saved into database.
     *
     * @param user
     */
    protected void onLoginFinalize(U user) {

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();

        U user = this.get(player.getUniqueId());
        if (user == null) {
            // this is a backup as login event is skipped sometimes.
            user = createNewUser(player.getUniqueId());
        }

        user.player = player;
        user.displayName = player.getName();

        try {
            onLoginFinalize(user);
        } finally {
            this.save(player.getUniqueId(), user);
        }
    }

    public static class PlayerWrapper
            extends PermissionHolder implements ManagerElementCaching.NamedElement, PluginEntity {
        final UUID uuid;

        UUID parentUuid;
        String displayName;

        transient OfflinePlayer player;

        public PlayerWrapper(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public UUID getUuid() {
            return uuid;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }

        @Override
        public UUID getParentUuid() {
            return parentUuid;
        }

        @Override
        protected void setParentUuid(UUID parentUuid) {
            this.parentUuid = parentUuid;
        }
    }
}
