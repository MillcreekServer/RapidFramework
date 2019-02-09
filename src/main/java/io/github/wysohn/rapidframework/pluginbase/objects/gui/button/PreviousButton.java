package io.github.wysohn.rapidframework.pluginbase.objects.gui.button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.manager.gui.Button;
import io.github.wysohn.rapidframework.pluginbase.manager.gui.Frame;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.frame.PageNodeFrame;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.handlers.button.ButtonEventHandler;

public class PreviousButton extends Button {
	public PreviousButton(PluginBase base, Frame parent) {
		super(base, parent, new ItemStack(Material.WOOL, 1, (short) 1));

		ClickEventHandler handler = new ClickEventHandler();
		this.setLeftClickEventHandler(handler);
		this.setRightClickEventHandler(handler);
		
		this.updateDisplayName(ChatColor.RED+"<");
	}

	private class ClickEventHandler implements ButtonEventHandler{
		@Override
		public void onClick(Player player) {
			Frame frame = getParent();
			if(!(frame instanceof PageNodeFrame))
				return;
			
			PageNodeFrame pagedFrame = (PageNodeFrame) frame;
			PageNodeFrame previousFrame = pagedFrame.getPrevious();
			if(previousFrame != null){
				previousFrame.show(player);
			}else{
				//UserInterfaceLib.sendMessage(player, Languages.Button_PagedFrame_OutOfBound);
			}
		}
	}
}
