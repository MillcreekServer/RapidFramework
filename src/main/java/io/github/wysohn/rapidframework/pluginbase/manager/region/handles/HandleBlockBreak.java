package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockBreakEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion.EventHandle;

public class HandleBlockBreak extends DefaultHandle implements AbstractManagerRegion.EventHandle<BlockBreakEvent> {
    public HandleBlockBreak(AbstractManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(BlockBreakEvent e) {
        return e.getPlayer();
    }

    @Override
    public Location getLocation(BlockBreakEvent e) {
        return e.getBlock().getLocation();
    }
}
