package io.github.wysohn.rapidframework.pluginbase.objects.gui.frame;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.manager.gui.Button;
import io.github.wysohn.rapidframework.pluginbase.manager.gui.Frame;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.button.FirstButton;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.button.LastButton;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.button.NextButton;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.button.PageButton;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.button.PreviousButton;

/**
 * Linked list style
 * @author wysohn
 *
 */
public class PageNodeFrame extends Frame {
	public final PageFrame manager;
	
	private final int index;
	private PageNodeFrame previous = null;
	private PageNodeFrame next = null;
	
	public PageNodeFrame(PluginBase base, PageFrame manager, String name, int index) {
		super(base, name+" Pg."+index, ChestSize.SIX);
		this.manager = manager;
		this.index = index;
		
		//fill line 6
		//0,3,5,8 - glass
		setButton(Button.getIndex(5, 0), new Button(base, this, new ItemStack(Material.STAINED_GLASS_PANE)){});
		setButton(Button.getIndex(5, 1), new FirstButton(base, this));
		setButton(Button.getIndex(5, 2), new PreviousButton(base, this));
		setButton(Button.getIndex(5, 3), new Button(base, this, new ItemStack(Material.STAINED_GLASS_PANE)){});
		setButton(Button.getIndex(5, 4), new PageButton(base, this).updateDisplayName(ChatColor.GREEN+"Pg. "+index));
		setButton(Button.getIndex(5, 5), new Button(base, this, new ItemStack(Material.STAINED_GLASS_PANE)){});
		setButton(Button.getIndex(5, 6), new NextButton(base, this));
		setButton(Button.getIndex(5, 7), new LastButton(base, this));
		setButton(Button.getIndex(5, 8), new Button(base, this, new ItemStack(Material.STAINED_GLASS_PANE)){});
	}

	public int getIndex() {
		return index;
	}

	public PageNodeFrame getPrevious() {
		return previous;
	}

	public PageNodeFrame getNext() {
		return next;
	}
	
	void setPrevious(PageNodeFrame previous) {
		this.previous = previous;
	}

	void setNext(PageNodeFrame next) {
		this.next = next;
	}
	
}
