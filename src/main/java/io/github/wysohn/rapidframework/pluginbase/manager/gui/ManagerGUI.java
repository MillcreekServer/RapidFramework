package io.github.wysohn.rapidframework.pluginbase.manager.gui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginManager;

public class ManagerGUI extends PluginManager<PluginBase> implements Listener{
	private static final Map<InventoryWrapper, Frame> registeredFrames = new ConcurrentHashMap<InventoryWrapper, Frame>();
	void registerFrame(Frame frame){
		registeredFrames.put(new InventoryWrapper(frame.getInstance()), frame);
		
		if(base.getPluginConfig().Plugin_Debugging)
			base.getLogger().info("GUI ["+frame.getInstance().hashCode()+"] is registered!");
	}
	void unregisterFrame(Frame frame){
		registeredFrames.remove(new InventoryWrapper(frame.getInstance()));
		
		if(base.getPluginConfig().Plugin_Debugging)
			base.getLogger().info("GUI ["+frame.getInstance().hashCode()+"] is unregistered!");
	}

	public ManagerGUI(PluginBase base, int loadPriority) {
		super(base, loadPriority);
	}
	
	@Override
	protected void onEnable() throws Exception {

	}
	@Override
	protected void onReload() throws Exception {

	}
	
	@Override
	public void onDisable() {

	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e){
		InventoryWrapper inv = new InventoryWrapper(e.getInventory());
		if(!registeredFrames.containsKey(inv))
			return;
		
		Frame frame = registeredFrames.get(inv);
		
		int rawSlot = e.getRawSlot();
		if(rawSlot >= 0 && rawSlot < frame.buttons.length){
			Button button = frame.buttons[rawSlot];
			if(button != null)
				button.handleEvent((InventoryClickEvent) e);
		}
	}
	
	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent e){
		InventoryWrapper inv = new InventoryWrapper(e.getInventory());
		if(!registeredFrames.containsKey(inv))
			return;
		
		Frame frame = registeredFrames.get(inv);

		if(!(e.getPlayer() instanceof Player))
			return;
		
		if(frame.closeEventHandler != null)
			frame.closeEventHandler.onClose((Player) e.getPlayer());
		
		unregisterFrame(frame);
	}

	
	private static class InventoryWrapper{
		Inventory inv;

		InventoryWrapper(Inventory inv) {
			this.inv = inv;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + inv.getName().hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof InventoryWrapper))
				return false;
			InventoryWrapper other = (InventoryWrapper) obj;
			if (inv == null) {
				if (other.inv != null)
					return false;
			} else if (!inv.equals(other.inv))
				return false;
			
			return true;
		}
	}
}