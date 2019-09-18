package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.objects.ClaimInfo;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockFromToEvent;

public class HandleBlockFromTo extends DefaultHandle implements AbstractManagerRegion.EventHandle<BlockFromToEvent> {
    public HandleBlockFromTo(AbstractManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(BlockFromToEvent e) {
        return null;
    }

    @Override
    public Location getLocation(BlockFromToEvent e) {
        ClaimInfo from = getInfo(e.getBlock().getLocation());
        ClaimInfo to = getInfo(e.getToBlock().getLocation());

        if (from == null && to == null) {
            return null;
        } else if (to != null) {
            if (from == null)
                return null;

            if (to.getArea().equals(from.getArea()))
                return null;

            return e.getToBlock().getLocation();
        }

        return null;
    }
}
