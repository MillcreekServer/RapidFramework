package io.github.wysohn.rapidframework.pluginbase.manager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginManager;
import io.github.wysohn.rapidframework.pluginbase.manager.event.PlayerEquipItemEvent;
import io.github.wysohn.rapidframework.pluginbase.manager.event.PlayerUnequipItemEvent;
import io.github.wysohn.rapidframework.utils.items.InventoryUtil;

public class ManagerEquipment extends PluginManager<PluginBase> implements Listener {

    private ManagerEquipment(PluginBase base, int loadPriority) {
	super(base, loadPriority);
    }

    @Override
    protected void onEnable() throws Exception {

    }

    @Override
    protected void onDisable() throws Exception {

    }

    @Override
    protected void onReload() throws Exception {

    }

    /**
     * Shift click on item set by clicking slot with item on cursor
     * 
     * @param ev
     */
    @EventHandler
    public void onEquip(InventoryClickEvent ev) {
	if (ev.getInventory().getType() != InventoryType.CRAFTING)
	    return;

	if (ev.isShiftClick()) {
	    int rawSlot = InventoryUtil.getRawSlotOfEquipment(ev.getCurrentItem());
	    ItemStack previousItem = ev.getView().getItem(rawSlot);
	    if (previousItem == null || previousItem.getType() == Material.AIR) {
		if (ev.getWhoClicked() instanceof Player) {
		    ev.setCancelled(onEquip((Player) ev.getWhoClicked(), ev.getCurrentItem()).isCancelled());
		}
	    }
	} else {
	    if (ev.getSlotType() != SlotType.ARMOR)
		return;

	    if (!InventoryUtil.isEquipment(ev.getCursor()))
		return;

	    if (ev.getRawSlot() != InventoryUtil.getRawSlotOfEquipment(ev.getCursor()))
		return;

	    if (ev.getWhoClicked() instanceof Player) {
		ev.setCancelled(onEquip((Player) ev.getWhoClicked(), ev.getCursor()).isCancelled());
	    }
	}
    }

    /**
     * Drag item to slot
     * 
     * @param ev
     */
    @EventHandler
    public void onEquip(InventoryDragEvent ev) {
	if (ev.getInventory().getType() != InventoryType.PLAYER)
	    return;

	if (!InventoryUtil.isEquipment(ev.getOldCursor()))
	    return;

	if (ev.getWhoClicked() instanceof Player) {
	    ev.setCancelled(onEquip((Player) ev.getWhoClicked(), ev.getOldCursor()).isCancelled());
	}
    }

    /**
     * Right click while item on hand
     * 
     * @param ev
     */
    @EventHandler
    public void onEquip(PlayerInteractEvent ev) {
	if (ev.getAction() != Action.RIGHT_CLICK_AIR && ev.getAction() != Action.RIGHT_CLICK_BLOCK)
	    return;

	ItemStack IS = ev.getPlayer().getItemInHand();
	if (IS != null && InventoryUtil.isEquipment(IS)) {
	    ev.setCancelled(onEquip(ev.getPlayer(), IS).isCancelled());
	}
    }

    /**
     * Dispense item to player. Just cancel as player is not tracked.
     * 
     * @param ev
     */
    @EventHandler
    public void onEquip(BlockDispenseEvent ev) {
	if (!InventoryUtil.isEquipment(ev.getItem()))
	    return;

	ev.setCancelled(true);
    }

    private PlayerEquipItemEvent onEquip(Player player, ItemStack item) {
	PlayerEquipItemEvent peie = new PlayerEquipItemEvent(player, item);
	Bukkit.getPluginManager().callEvent(peie);
	return peie;
    }

    /**
     * DROP_ONE_SLOT (when cursor is on armor and q is pressed) click on the item in
     * armor slot
     * 
     * @param ev
     */
    @EventHandler
    public void onUnequip(InventoryClickEvent ev) {
	if (!(ev.getWhoClicked() instanceof Player))
	    return;

	if (ev.getAction() == InventoryAction.DROP_ONE_SLOT && ev.getSlotType() == SlotType.ARMOR) {
	    ev.setCancelled(onUnequip((Player) ev.getWhoClicked(), ev.getCurrentItem()).isCancelled());
	} else if (ev.getSlotType() == SlotType.ARMOR && InventoryUtil.isEquipment(ev.getCurrentItem())) {
	    ev.setCancelled(onUnequip((Player) ev.getWhoClicked(), ev.getCurrentItem()).isCancelled());
	}
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUnequip(PlayerDeathEvent ev) {
	if(ev.getKeepInventory())
	    return;
	
	onUnequip(ev.getEntity(), ev.getEntity().getInventory().getHelmet());
	onUnequip(ev.getEntity(), ev.getEntity().getInventory().getChestplate());
	onUnequip(ev.getEntity(), ev.getEntity().getInventory().getLeggings());
	onUnequip(ev.getEntity(), ev.getEntity().getInventory().getBoots());
    }

    private PlayerUnequipItemEvent onUnequip(Player player, ItemStack item) {
	if(item == null || item.getType() == Material.AIR)
	    return new PlayerUnequipItemEvent(player, item);
	
	PlayerUnequipItemEvent puie = new PlayerUnequipItemEvent(player, item);
	Bukkit.getPluginManager().callEvent(puie);
	return puie;
    }
    
    private static ManagerEquipment shared = null;
    public static ManagerEquipment getSharedInstance(PluginBase base, int loadPriority) {
	if(shared == null) {
	    shared = new ManagerEquipment(base, loadPriority);
	}
	return shared;
    }
}
