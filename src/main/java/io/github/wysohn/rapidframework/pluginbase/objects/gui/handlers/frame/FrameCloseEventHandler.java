package io.github.wysohn.rapidframework.pluginbase.objects.gui.handlers.frame;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface FrameCloseEventHandler extends FrameEvent {
    void onClose(Player player);
}
