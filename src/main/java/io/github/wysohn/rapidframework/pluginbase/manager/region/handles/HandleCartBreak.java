package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion.EventHandle;

public class HandleCartBreak extends DefaultHandle implements AbstractManagerRegion.EventHandle<VehicleDestroyEvent> {
    public HandleCartBreak(AbstractManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(VehicleDestroyEvent e) {
        return e.getAttacker();
    }

    @Override
    public Location getLocation(VehicleDestroyEvent e) {
        return e.getVehicle().getLocation();
    }
}
