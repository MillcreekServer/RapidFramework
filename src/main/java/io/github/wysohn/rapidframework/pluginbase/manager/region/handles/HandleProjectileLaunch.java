package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion.EventHandle;

public class HandleProjectileLaunch extends DefaultHandle implements AbstractManagerRegion.EventHandle<ProjectileLaunchEvent> {
    public HandleProjectileLaunch(AbstractManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(ProjectileLaunchEvent e) {
        return (Entity) e.getEntity().getShooter();
    }

    @Override
    public Location getLocation(ProjectileLaunchEvent e) {
        return e.getEntity().getLocation();
    }
}
