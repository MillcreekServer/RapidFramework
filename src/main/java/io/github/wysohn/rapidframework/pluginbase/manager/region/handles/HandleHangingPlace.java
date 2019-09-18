package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.hanging.HangingPlaceEvent;

public class HandleHangingPlace extends DefaultHandle implements AbstractManagerRegion.EventHandle<HangingPlaceEvent> {
    public HandleHangingPlace(AbstractManagerRegion rmanager) {
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
