package io.github.wysohn.rapidframework.pluginbase.objects.gui.button;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.manager.gui.Button;
import io.github.wysohn.rapidframework.pluginbase.manager.gui.Frame;

public class PageButton extends Button{

	public PageButton(PluginBase base, Frame parent) {
		super(base, parent, new ItemStack(Material.BOOK_AND_QUILL, 1));
		// TODO Auto-generated constructor stub
	}

}
