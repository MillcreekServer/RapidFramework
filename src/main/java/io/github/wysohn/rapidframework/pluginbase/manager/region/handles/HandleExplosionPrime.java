package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion.EventHandle;

public class HandleExplosionPrime extends DefaultHandle implements AbstractManagerRegion.EventHandle<ExplosionPrimeEvent> {
    public HandleExplosionPrime(AbstractManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(ExplosionPrimeEvent e) {
        return null;
    }

    @Override
    public Location getLocation(ExplosionPrimeEvent e) {
        return e.getEntity().getLocation();
    }
}
