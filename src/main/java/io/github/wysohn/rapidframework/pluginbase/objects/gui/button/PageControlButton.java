package io.github.wysohn.rapidframework.pluginbase.objects.gui.button;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.manager.gui.ManagerGUI;
import org.bukkit.inventory.ItemStack;

public class PageControlButton extends ManagerGUI.Button {
    protected PluginBase base;

    public PageControlButton(PluginBase base, ManagerGUI.Frame parent, ItemStack IS) {
        super(parent, IS);
        this.base = base;
    }
}
