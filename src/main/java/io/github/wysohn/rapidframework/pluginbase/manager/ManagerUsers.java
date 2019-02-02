package io.github.wysohn.rapidframework.pluginbase.manager;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;

public abstract class ManagerUsers<U extends ManagerUsers.User> extends ManagerElementCaching<UUID, U>
	implements Listener{

	public ManagerUsers(PluginBase base, int loadPriority) {
		super(base, loadPriority);
	}

	@Override
	protected String getTableName() {
		return "Users";
	}

	@Override
	protected UUID createKeyFromString(String str) {
		return UUID.fromString(str);
	}
	
	/**
	 * This call is asynchronous. Create new user instance and return it.
	 * @param uuid
	 * @return
	 */
	protected abstract U createNewUser(UUID uuid);

	@Override
	protected CacheUpdateHandle<UUID, U> getUpdateHandle() {
		return null;
	}

	@Override
	protected CacheDeleteHandle<UUID, U> getDeleteHandle() {
		return null;
	}
	
	@EventHandler
	public void onLogin(AsyncPlayerPreLoginEvent ev) {
		U user = this.get(ev.getUniqueId());
		if(user == null) {
			user = createNewUser(ev.getUniqueId());
			this.save(ev.getUniqueId(), user);
		}
	}
	
	/**
	 * If anything has to be done with user instance after it is loaded,
	 * do it here.
	 * @param user
	 */
	protected abstract void onLoginFinalize(U user);
	
	@EventHandler
	public void onJoin(PlayerJoinEvent ev) {
		Player player = ev.getPlayer();
		
		U user = this.get(player.getUniqueId());
		if(user == null) {
			user = createNewUser(player.getUniqueId());
		}
		
		user.player = player;
		this.save(player.getUniqueId(), user);
	}
	
	public static class User implements ManagerElementCaching.NamedElement{
		transient OfflinePlayer player;

		public OfflinePlayer getPlayer() {
			return player;
		}

		@Override
		public String getName() {
			return player.getName();
		}
		
	}
}
