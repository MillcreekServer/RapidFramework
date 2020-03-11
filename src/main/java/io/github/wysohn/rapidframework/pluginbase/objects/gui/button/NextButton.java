package io.github.wysohn.rapidframework.pluginbase.objects.gui.button;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.manager.gui.ManagerGUI;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.frame.PageNodeFrame;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.handlers.button.ButtonEventHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NextButton extends PageControlButton {
    @SuppressWarnings("deprecation")
    public NextButton(PluginBase base, ManagerGUI.Frame parent) {
        super(base, parent, new ItemStack(Material.WHITE_WOOL, 1));

        ClickEventHandler handler = new ClickEventHandler();
        this.setLeftClickEventHandler(handler);
        this.setRightClickEventHandler(handler);

        this.updateDisplayName(ChatColor.RED + ">");
    }

    private class ClickEventHandler implements ButtonEventHandler {
        @Override
        public void onClick(Player player) {
            ManagerGUI.Frame frame = getParent();
            if (!(frame instanceof PageNodeFrame))
                return;

            PageNodeFrame pagedFrame = (PageNodeFrame) frame;
            PageNodeFrame nextFrame = pagedFrame.getNext();
            if (nextFrame != null) {
                nextFrame.show(player);
            } else {
                // UserInterfaceLib.sendMessage(player, Languages.Button_PagedFrame_OutOfBound);
            }
        }
    }
}
