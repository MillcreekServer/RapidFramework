package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion.EventHandle;

public class HandleHopperMoveItem extends DefaultHandle implements ManagerRegion.EventHandle<InventoryMoveItemEvent> {
    public HandleHopperMoveItem(ManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(InventoryMoveItemEvent e) {
        return null;
    }

    @Override
    public Location getLocation(InventoryMoveItemEvent e) {
        return e.getSource().getLocation();
    }
}
