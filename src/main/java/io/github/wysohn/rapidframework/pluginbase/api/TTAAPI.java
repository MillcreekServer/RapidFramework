package io.github.wysohn.rapidframework.pluginbase.api;

import de.Herbystar.TTA.TTA_Methods;
import io.github.wysohn.rapidframework.pluginbase.PluginAPISupport.APISupport;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TTAAPI extends APISupport {

    public TTAAPI(PluginBase base) {
        super(base);
    }

    @Override
    public boolean init() throws Exception {
        return Bukkit.getPluginManager().isPluginEnabled(getPluginName());
    }

    public void sendActionBar(Player player, String message) {
        TTA_Methods.sendActionBar(player, message);
    }

    public void sendActionBar(Player player, String message, int tick) {
        TTA_Methods.sendActionBar(player, message, tick);
    }
}
