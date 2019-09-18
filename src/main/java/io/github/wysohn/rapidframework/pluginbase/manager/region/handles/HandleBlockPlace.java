package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockPlaceEvent;

public class HandleBlockPlace extends DefaultHandle implements AbstractManagerRegion.EventHandle<BlockPlaceEvent> {
    public HandleBlockPlace(AbstractManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(BlockPlaceEvent e) {
        return e.getPlayer();
    }

    @Override
    public Location getLocation(BlockPlaceEvent e) {
        return e.getBlock().getLocation();
    }
}
