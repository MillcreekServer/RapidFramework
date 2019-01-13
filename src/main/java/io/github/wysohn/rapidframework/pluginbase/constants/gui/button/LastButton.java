package io.github.wysohn.rapidframework.pluginbase.constants.gui.button;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.constants.gui.frame.PageNodeFrame;
import io.github.wysohn.rapidframework.pluginbase.constants.gui.handlers.button.ButtonEventHandler;
import io.github.wysohn.rapidframework.pluginbase.language.DefaultLanguages;
import io.github.wysohn.rapidframework.pluginbase.manager.gui.Button;
import io.github.wysohn.rapidframework.pluginbase.manager.gui.Frame;

public class LastButton extends Button {

	@SuppressWarnings("deprecation")
	public LastButton(PluginBase base, Frame parent) {
		super(base, parent, new ItemStack(Material.WOOL, 1, (short) 5));

		ClickEventHandler handler = new ClickEventHandler();
		this.setLeftClickEventHandler(handler);
		this.setRightClickEventHandler(handler);
		
		this.updateDisplayName(ChatColor.RED+">>");
	}
	
	private class ClickEventHandler implements ButtonEventHandler{
		@Override
		public void onClick(Player player) {
			Frame frame = getParent();
			if(!(frame instanceof PageNodeFrame))
				return;
			
			PageNodeFrame pagedFrame = (PageNodeFrame) frame;
			PageNodeFrame lastFrame = pagedFrame.manager.getTail();
			if(lastFrame != null){
				lastFrame.show(player);
			}else{
				base.sendMessage(player, DefaultLanguages.GUI_Button_PagedFrame_OutOfBound);
			}
		}
	}
}
