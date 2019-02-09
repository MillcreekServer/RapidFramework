package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion.EventHandle;
import io.github.wysohn.rapidframework.pluginbase.objects.ClaimInfo;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

public class HandleHopperPickupItem extends DefaultHandle implements ManagerRegion.EventHandle<InventoryPickupItemEvent> {
    public HandleHopperPickupItem(ManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(InventoryPickupItemEvent e) {
        return null;
    }

    @Override
    public Location getLocation(InventoryPickupItemEvent e) {
        ClaimInfo claimItem = getInfo(e.getItem().getLocation());
        ClaimInfo claimInv = getInfo(e.getInventory().getLocation());

        if (claimItem != null && claimInv != null) {
            if (claimItem.getArea().equals(claimInv.getArea())) {
                return null;
            }
        }

        return e.getInventory().getLocation();
    }
}
