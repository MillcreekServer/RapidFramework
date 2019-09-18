package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.manager.event.PlayerBlockLocationEvent;
import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.objects.ClaimInfo;
import io.github.wysohn.rapidframework.utils.locations.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class HandleLocationBlockMove extends DefaultHandle
        implements AbstractManagerRegion.EventHandle<PlayerBlockLocationEvent> {
    public HandleLocationBlockMove(AbstractManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(PlayerBlockLocationEvent e) {
        return e.getPlayer();
    }

    @Override
    public Location getLocation(PlayerBlockLocationEvent e) {
        ClaimInfo from = getInfo(e.getFrom());
        ClaimInfo to = getInfo(e.getTo());

        if (from != null) {// prevent area exit
            if (to == null)
                return LocationUtil.convertToBukkitLocation(e.getFrom());

            // both direction
            if (!from.getArea().equals(to.getArea()))
                return LocationUtil.convertToBukkitLocation(e.getFrom());

            return null;
        } else if (to != null) {// prevent area enter
            // from is always null
            return LocationUtil.convertToBukkitLocation(e.getTo());
        } else {// nothing
            return null;
        }
    }
}
