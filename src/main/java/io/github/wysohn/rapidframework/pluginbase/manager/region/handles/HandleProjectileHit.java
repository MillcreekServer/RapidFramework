package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ProjectileHitEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion.EventHandle;

public class HandleProjectileHit extends DefaultHandle implements ManagerRegion.EventHandle<ProjectileHitEvent> {
    public HandleProjectileHit(ManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(ProjectileHitEvent e) {
        return (Entity) e.getEntity().getShooter();
    }

    @Override
    public Location getLocation(ProjectileHitEvent e) {
        return e.getEntity().getLocation();
    }
}
