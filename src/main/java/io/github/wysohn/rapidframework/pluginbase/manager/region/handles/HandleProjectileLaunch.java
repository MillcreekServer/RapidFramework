package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion.EventHandle;

public class HandleProjectileLaunch extends DefaultHandle implements ManagerRegion.EventHandle<ProjectileLaunchEvent> {
    public HandleProjectileLaunch(ManagerRegion rmanager) {
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
