package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockBurnEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion.EventHandle;

public class HandleBlockBurn extends DefaultHandle implements ManagerRegion.EventHandle<BlockBurnEvent> {
    public HandleBlockBurn(ManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(BlockBurnEvent e) {
        return null;
    }

    @Override
    public Location getLocation(BlockBurnEvent e) {
        return e.getBlock().getLocation();
    }
}
