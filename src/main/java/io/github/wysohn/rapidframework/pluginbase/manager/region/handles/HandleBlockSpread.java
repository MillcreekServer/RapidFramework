package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion.EventHandle;

public class HandleBlockSpread extends DefaultHandle implements AbstractManagerRegion.EventHandle {
    public HandleBlockSpread(AbstractManagerRegion rmanager) {
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
