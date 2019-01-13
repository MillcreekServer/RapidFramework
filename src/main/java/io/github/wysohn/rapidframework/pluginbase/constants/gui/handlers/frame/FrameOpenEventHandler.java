package io.github.wysohn.rapidframework.pluginbase.constants.gui.handlers.frame;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface FrameOpenEventHandler extends FrameEvent {
	/**
	 * DO NOT CALL showTo() in this method
	 * @param player
	 */
	void onOpen(Player player);
}
