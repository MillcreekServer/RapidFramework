package io.github.wysohn.rapidframework.pluginbase.objects.gui.frame;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.manager.gui.Button;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.handlers.frame.FrameCloseEventHandler;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.handlers.frame.FrameOpenEventHandler;

public class ButtonPageFrame extends PageFrame {
	private final Button[] buttons;
	
	private final Button[][] pages;
	
	public ButtonPageFrame(PluginBase base, String name, Button[] items) {
		super(base, name);
		Validate.notNull(items);
		
		this.buttons = items;
		this.pages = divide(items);
		
		initButtonPages();
	}

	private final int MAXLENGTH = 5*9;
	private Button[][] divide(Button[] items){
		int pagecount = items.length / MAXLENGTH;
		int leftover = items.length % MAXLENGTH;
		pagecount = leftover == 0 ? pagecount : pagecount + 1;
		
		Button[][] pages = new Button[pagecount][MAXLENGTH];
		
		for(int i = 0; i < items.length ; i++){
			pages[i / MAXLENGTH][i % MAXLENGTH] = items[i];
		}
		
		return pages;
	}
	
	public Button[] getItems(){
		return merge(pages);
	}
	
	private Button[] merge(Button[][] pages){
		int pagecount = buttons.length / MAXLENGTH;
		int leftover = buttons.length % MAXLENGTH;
		pagecount = leftover == 0 ? pagecount : pagecount + 1;
		
		Button[] merged = new Button[pagecount * MAXLENGTH];
		
		for(int i = 0; i < pagecount * MAXLENGTH; i++){
			merged[i] = pages[i / MAXLENGTH][i % MAXLENGTH];
		}
		
		return merged;
	}
	
	private void initButtonPages(){
		for(final Button[] page : pages){
			final PageNodeFrame frame = add();
			frame.setOpenEventHandler(new FrameOpenEventHandler(){
				@Override
				public void onOpen(Player player) {
					for(int i = 0; i < MAXLENGTH; i++){
						if(page[i] != null)
							page[i].setParent(frame);
						frame.setButton(i, page[i]);
					}
				}
			});

			frame.setCloseEventHandler(new FrameCloseEventHandler(){
				@Override
				public void onClose(Player player) {
					for(int i = 0; i < MAXLENGTH; i++){
						page[i] = frame.getButton(i);
					}
					
					ButtonPageFrame.this.closeHandler.onClose(player);
				}
			});
		}
	}
}
