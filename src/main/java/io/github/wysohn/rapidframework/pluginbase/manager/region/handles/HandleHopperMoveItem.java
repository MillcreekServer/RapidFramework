package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class HandleHopperMoveItem extends DefaultHandle
        implements AbstractManagerRegion.EventHandle<InventoryMoveItemEvent> {
    public HandleHopperMoveItem(AbstractManagerRegion rmanager) {
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
