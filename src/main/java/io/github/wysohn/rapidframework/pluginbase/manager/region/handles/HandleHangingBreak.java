package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class HandleHangingBreak extends DefaultHandle
        implements AbstractManagerRegion.EventHandle<HangingBreakByEntityEvent> {
    public HandleHangingBreak(AbstractManagerRegion rmanager) {
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
