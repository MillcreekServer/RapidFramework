package io.github.wysohn.rapidframework.pluginbase.api;

import io.github.wysohn.rapidframework.pluginbase.PluginAPISupport;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import org.bukkit.entity.Player;

public class PlaceholderAPI extends PluginAPISupport.APISupport {
    public PlaceholderAPI(PluginBase base) {
        super(base);
    }

    @Override
    public boolean init() throws Exception {
        return true;
    }

    public String parse(Player player, String msg){
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, msg);
    }
}
