package io.github.wysohn.rapidframework.pluginbase.constants.structure.interfaces.interact;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.constants.structure.interfaces.filter.EntityFilter;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public interface Clickable {
    void onClick(PluginBase base, Action action, Player player, EntityFilter<Player> filter);
}
