package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.hanging.HangingPlaceEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion.EventHandle;

public class HandleHangingPlace extends DefaultHandle implements ManagerRegion.EventHandle<HangingPlaceEvent> {
    public HandleHangingPlace(ManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(HangingPlaceEvent e) {
        return e.getPlayer();
    }

    @Override
    public Location getLocation(HangingPlaceEvent e) {
        return e.getBlock().getLocation();
    }
}
