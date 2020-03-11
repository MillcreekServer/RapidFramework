package io.github.wysohn.rapidframework.pluginbase.objects.gui.button;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.manager.gui.ManagerGUI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PageButton extends PageControlButton {

    public PageButton(PluginBase base, ManagerGUI.Frame parent) {
        super(base, parent, new ItemStack(Material.WHITE_WOOL, 1));
        // TODO Auto-generated constructor stub
    }

}
