package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion.EventHandle;
import io.github.wysohn.rapidframework.pluginbase.objects.ClaimInfo;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Iterator;

public class HandleEntityExplode extends DefaultHandle implements ManagerRegion.EventHandle<EntityExplodeEvent> {
    public HandleEntityExplode(ManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(EntityExplodeEvent e) {
        return null;
    }

    @Override
    public Location getLocation(EntityExplodeEvent e) {
        for(Iterator<Block> iter = e.blockList().iterator(); iter.hasNext();){
            Location location = iter.next().getLocation();
            ClaimInfo claimInfo = getInfo(location);
            if(claimInfo != null)
                return location;
        }

        return null;
    }
}
