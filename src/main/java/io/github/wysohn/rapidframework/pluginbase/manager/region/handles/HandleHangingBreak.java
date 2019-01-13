package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion.EventHandle;

public class HandleHangingBreak extends DefaultHandle implements ManagerRegion.EventHandle<HangingBreakByEntityEvent> {
    public HandleHangingBreak(ManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(HangingBreakByEntityEvent e) {
        return e.getRemover();
    }

    @Override
    public Location getLocation(HangingBreakByEntityEvent e) {
        return e.getEntity().getLocation();
    }
}
