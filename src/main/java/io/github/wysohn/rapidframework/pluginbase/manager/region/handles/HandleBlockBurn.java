package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockBurnEvent;

public class HandleBlockBurn extends DefaultHandle implements AbstractManagerRegion.EventHandle<BlockBurnEvent> {
    public HandleBlockBurn(AbstractManagerRegion rmanager) {
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
