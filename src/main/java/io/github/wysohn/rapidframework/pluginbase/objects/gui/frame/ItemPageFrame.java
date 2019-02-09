package io.github.wysohn.rapidframework.pluginbase.objects.gui.frame;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.handlers.frame.FrameCloseEventHandler;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.handlers.frame.FrameOpenEventHandler;

public class ItemPageFrame extends PageFrame {
	private final ItemStack[] items;
	
	private final ItemStack[][] inv;
	
	public ItemPageFrame(PluginBase base, String name, ItemStack[] items) {
		super(base, name);
		Validate.notNull(items);
		
		this.items = items;
		this.inv = divide(items);
		
		initItemPages();
	}

	private final int MAXLENGTH = 5*9;
	private ItemStack[][] divide(ItemStack[] items){
		int pagecount = items.length / MAXLENGTH;
		int leftover = items.length % MAXLENGTH;
		pagecount = leftover == 0 ? pagecount : pagecount + 1;
		
		ItemStack[][] pages = new ItemStack[pagecount][MAXLENGTH];
		
		for(int i = 0; i < items.length ; i++){
			pages[i / MAXLENGTH][i % MAXLENGTH] = items[i];
		}
		
		return pages;
	}
	
	public ItemStack[] getItems(){
		return merge(inv);
	}
	
	private ItemStack[] merge(ItemStack[][] pages){
		int pagecount = items.length / MAXLENGTH;
		int leftover = items.length % MAXLENGTH;
		pagecount = leftover == 0 ? pagecount : pagecount + 1;
		
		ItemStack[] merged = new ItemStack[pagecount * MAXLENGTH];
		
		for(int i = 0; i < pagecount * MAXLENGTH; i++){
			merged[i] = pages[i / MAXLENGTH][i % MAXLENGTH];
		}
		
		return merged;
	}
	
	private void initItemPages(){
		for(final ItemStack[] page : inv){
			final PageNodeFrame frame = add();
			frame.setOpenEventHandler(new FrameOpenEventHandler(){
				@Override
				public void onOpen(Player player) {
					for(int i = 0; i < MAXLENGTH; i++){
						frame.getInventory().setItem(i, page[i]);
					}
				}
			});

			frame.setCloseEventHandler(new FrameCloseEventHandler(){
				@Override
				public void onClose(Player player) {
					for(int i = 0; i < MAXLENGTH; i++){
						page[i] = frame.getInventory().getItem(i);
					}
					
					ItemPageFrame.this.closeHandler.onClose(player);
				}
			});
		}
	}
}
