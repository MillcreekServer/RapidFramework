package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.objects.ClaimInfo;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockPistonExtendEvent;

import java.util.Iterator;

public class HandlePistonExtend extends DefaultHandle
        implements AbstractManagerRegion.EventHandle<BlockPistonExtendEvent> {
    public HandlePistonExtend(AbstractManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(BlockPistonExtendEvent e) {
        return null;
    }

    @Override
    public Location getLocation(BlockPistonExtendEvent e) {
        for (Iterator<Block> iter = e.getBlocks().iterator(); iter.hasNext(); ) {
            Location location = iter.next().getLocation();
            ClaimInfo info = getInfo(location);
            if (info != null)
                return location;
        }
        return null;
    }
}
