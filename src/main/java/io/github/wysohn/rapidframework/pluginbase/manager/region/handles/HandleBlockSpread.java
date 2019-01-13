package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion.EventHandle;

public class HandleBlockSpread extends DefaultHandle implements ManagerRegion.EventHandle {
    public HandleBlockSpread(ManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(Event e) {
        return null;
    }

    @Override
    public Location getLocation(Event e) {
        return null;
    }
}
