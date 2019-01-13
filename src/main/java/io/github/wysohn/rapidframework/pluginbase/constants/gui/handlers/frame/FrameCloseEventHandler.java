package io.github.wysohn.rapidframework.pluginbase.constants.gui.handlers.frame;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface FrameCloseEventHandler extends FrameEvent {
	void onClose(Player player);
}
