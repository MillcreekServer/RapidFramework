package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ProjectileHitEvent;

public class HandleProjectileHit extends DefaultHandle
        implements AbstractManagerRegion.EventHandle<ProjectileHitEvent> {
    public HandleProjectileHit(AbstractManagerRegion rmanager) {
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
