package io.github.wysohn.rapidframework.pluginbase.manager.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.constants.gui.handlers.frame.FrameCloseEventHandler;
import io.github.wysohn.rapidframework.pluginbase.constants.gui.handlers.frame.FrameOpenEventHandler;

/**
 * Frames register itself automatically as constructor is called
 * @author wysohn
 *
 */
public class Frame{
	protected transient final PluginBase base;
	
	transient final String name;
	transient final ChestSize size;
	
	transient final Inventory instance;
	
	transient Button[] buttons;
	
	transient FrameOpenEventHandler openEventHandler;
	transient FrameCloseEventHandler closeEventHandler;
	/**
	 *
	 * @param name unique name for inventory; shouldn't be null
	 * @param size size of inventory; shouldn't be null
	 */
	protected Frame(PluginBase base, String name, ChestSize size){
		this.base = base;
		this.name = name;
		this.size = size;
		
		buttons = new Button[size.size];
		
		instance = Bukkit.createInventory(null, size.size, name);
	}
	public String getName() {
		return name;
	}
	
	public Inventory getInstance() {
		return instance;
	}
	/**
	 * 
	 * @param btn
	 * @return magic value
	 * @deprecated use setButton()
	 */
	public int addButton(Button btn){
		return -1;
	}
	
	public ChestSize getSize() {
		return size;
	}
	
	public void setButton(int index, Button btn){
		buttons[index] = btn;
	}
	
	public Button getButton(int index){
		return buttons[index];
	}
	
	public void setOpenEventHandler(FrameOpenEventHandler openEventHandler) {
		this.openEventHandler = openEventHandler;
	}
	public void setCloseEventHandler(FrameCloseEventHandler closeEventHandler) {
		this.closeEventHandler = closeEventHandler;
	}
	
	public void clear(){
		for(int i = 0; i < buttons.length - 1; i++){
			buttons[i] = null;
		}
	}
	
	public Inventory getInventory(){
		return instance;
	}
	
	public void show(Player viewer) {
		ManagerGUI manager = base.getManager(ManagerGUI.class);
		instance.clear();

		if(openEventHandler != null){
			openEventHandler.onOpen(viewer);
		}
		
		for(int i=0;i<size.size;i++){
			if(buttons[i] == null)
				continue;
			
			instance.setItem(i, buttons[i].getIS());
		}
		
		viewer.openInventory(instance);
		manager.registerFrame(this);
	}
	
	public enum ChestSize{
		ONE(9), TWO(18), THREE(27), FOUR(36), FIVE(45), SIX(54);
		private final int size;
		private ChestSize(int size){
			this.size = size;
		}
		public int getSize() {
			return size;
		}
	}
	
	public static class Builder{
		private PluginBase base;
		private Frame frame;
		private Player viewer;
		private Builder(PluginBase base, Player viewer, String title, ChestSize size) {
			this.base = base;
			this.frame = new Frame(base, title, size);
			this.viewer = viewer;
		}
		
		public static Builder newWith(PluginBase base, Player viewer, String title, ChestSize size) {
			return new Builder(base, viewer, title, size);
		}
		
		public Builder setButton(int row, int col, Button button) {
			frame.setButton(Button.getIndex(row, col), button);
			return this;
		}
		
		public Builder onOpenReact(FrameOpenEventHandler handler) {
			frame.openEventHandler = handler;
			return this;
		}
		
		public Builder onCloseReact(FrameCloseEventHandler handler) {
			frame.closeEventHandler = handler;
			return this;
		}
		
		public Frame build() {
			return frame;
		}
	}
}
