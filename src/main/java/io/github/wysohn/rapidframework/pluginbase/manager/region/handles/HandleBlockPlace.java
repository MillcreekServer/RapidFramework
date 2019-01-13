package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockPlaceEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion.EventHandle;

public class HandleBlockPlace extends DefaultHandle implements ManagerRegion.EventHandle<BlockPlaceEvent> {
    public HandleBlockPlace(ManagerRegion rmanager) {
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
