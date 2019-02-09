package io.github.wysohn.rapidframework.pluginbase.objects.gui.handlers.button;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface ButtonEventHandler {
	void onClick(Player player);
}
