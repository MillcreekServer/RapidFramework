package io.github.wysohn.rapidframework.pluginbase.api;

import com.connorlinfoot.titleapi.TitleAPI;
import io.github.wysohn.rapidframework.pluginbase.PluginAPISupport.APISupport;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import org.bukkit.entity.Player;

public class TitleAPISupport extends APISupport {

    public TitleAPISupport(PluginBase base) {
        super(base);
    }

    @Override
    public boolean init() throws Exception {
        return true;
    }

    public void sendTitle(Player player, int fadeIn, int stay, int fadeOut, String title, String subtitle) {
        TitleAPI.sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
    }

    public void sendTabList(Player player, String header, String footer) {
        TitleAPI.sendTabTitle(player, header, footer);
    }

    public void clearTitle(Player player) {
        TitleAPI.clearTitle(player);
    }
}
