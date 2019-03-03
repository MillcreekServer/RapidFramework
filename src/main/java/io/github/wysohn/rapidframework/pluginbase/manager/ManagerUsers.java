package io.github.wysohn.rapidframework.pluginbase.manager;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.objects.permissions.PermissionHolder;

public abstract class ManagerUsers<PB extends PluginBase, U extends ManagerUsers.User>
	extends ManagerElementCaching<PB, UUID, U> implements Listener {

    public ManagerUsers(PB base, int loadPriority, Class<U> type) {
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
	user.player = Bukkit.getOfflinePlayer(ev.getUniqueId());
	user.lastKnownName = ev.getName();
	this.save(ev.getUniqueId(), user);
    }

    /**
     * If anything has to be done with user instance after it is loaded, do it here.
     * 
     * @param user
     */
    protected abstract void onLoginFinalize(U user);

    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
	Player player = ev.getPlayer();

	U user = this.get(player.getUniqueId());
	if (user == null) {
	    // this is a backup as login event is skipped sometimes.
	    user = createNewUser(player.getUniqueId());
	}

	user.player = player;
	user.lastKnownName = player.getName();
	this.save(player.getUniqueId(), user);
    }

    public static class User implements ManagerElementCaching.NamedElement, PermissionHolder {
	transient OfflinePlayer player;

	String lastKnownName = "";

	private UUID parentUuid;

	public OfflinePlayer getPlayer() {
	    return player;
	}

	public String getLastKnownName() {
	    return lastKnownName;
	}

	@Override
	public String getName() {
	    return lastKnownName;
	}

	@Override
	public UUID getUuid() {
	    return player.getUniqueId();
	}

	@Override
	public UUID getParentUuid() {
	    return parentUuid;
	}

	@Override
	public void setParentUuid(UUID uuid) {
	    parentUuid = uuid;
	}
    }
}
