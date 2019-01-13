package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockBreakEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion.EventHandle;

public class HandleBlockBreak extends DefaultHandle implements ManagerRegion.EventHandle<BlockBreakEvent> {
    public HandleBlockBreak(ManagerRegion rmanager) {
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
