package io.github.wysohn.rapidframework.pluginbase.api;

import io.github.wysohn.rapidframework.pluginbase.PluginAPISupport;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import me.clip.placeholderapi.PlaceholderHook;

import java.util.Optional;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderAPI extends PluginAPISupport.APISupport {
    public PlaceholderAPI(PluginBase base) {
	super(base);
    }

    @Override
    public boolean init() throws Exception {
	return true;
    }

    public String parse(Player player, String msg) {
	return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, msg);
    }

    /**
     * Register new placeholder. the lower case value of this plugin's name (in
     * bukkit.yml) will be used as identifier. If your plugin name is MyPlugin, then
     * it will be %myplugin_some_thing_...%
     */
    public void register(Placeholder placeholder) {
	me.clip.placeholderapi.PlaceholderAPI.registerPlaceholderHook(base.getName().toLowerCase(),
		new PlaceholderHook() {

		    @Override
		    public String onPlaceholderRequest(Player p, String params) {
			return placeholder.parse(Optional.ofNullable(p), params);
		    }

		});
    }

    @FunctionalInterface
    public interface Placeholder {
	String parse(Optional<Player> p, String params);
    }
}
