package io.github.wysohn.rapidframework3.bukkit.manager.api;

import io.github.wysohn.rapidframework3.bukkit.data.BukkitPlayer;
import io.github.wysohn.rapidframework3.bukkit.data.BukkitWrapper;
import io.github.wysohn.rapidframework3.core.api.ExternalAPI;
import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.interfaces.ICommandSender;
import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PlaceholderAPI extends ExternalAPI {
    public PlaceholderAPI(PluginMain main, String pluginName) {
        super(main, pluginName);
    }

    @Override
    public void enable() throws Exception {

    }

    @Override
    public void load() throws Exception {

    }

    @Override
    public void disable() throws Exception {

    }

    public String parse(ICommandSender player, String msg) {
        if(player instanceof BukkitPlayer)
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(((BukkitPlayer) player).getSender(), msg);
        else
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(null, msg);
    }

    /**
     * Register new placeholder. the lower case value of this plugin's name (in
     * bukkit.yml) will be used as identifier. If your plugin name is MyPlugin, then
     * it will be %myplugin_some_thing_...%
     */
    public void register(Placeholder placeholder) {
        register(main.getPluginName().toLowerCase(), placeholder);
    }

    public void register(String prefix, Placeholder placeholder) {
        me.clip.placeholderapi.PlaceholderAPI.registerPlaceholderHook(prefix,
                new PlaceholderHook() {

                    @Override
                    public String onPlaceholderRequest(Player p, String params) {
                        return placeholder.parse(Optional.ofNullable(p)
                                        .flatMap(player -> Optional.of(p)
                                                .map(BukkitWrapper::player))
                                        .orElse(null),
                                params);
                    }

                });
    }

    @FunctionalInterface
    public interface Placeholder {
        String parse(ICommandSender p, String params);
    }
}
