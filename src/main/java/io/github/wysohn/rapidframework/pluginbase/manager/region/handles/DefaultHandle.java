package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.constants.ClaimInfo;
import io.github.wysohn.rapidframework.pluginbase.constants.SimpleLocation;
import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion;
import io.github.wysohn.rapidframework.utils.locations.LocationUtil;
import org.bukkit.Location;

public abstract class DefaultHandle {
    private final ManagerRegion rmanager;

    protected DefaultHandle(ManagerRegion rmanager) {
        this.rmanager = rmanager;
    }

    protected ClaimInfo getInfo(SimpleLocation simpleLocation){
        return rmanager.getAreaInfo(simpleLocation);
    }

    protected ClaimInfo getInfo(Location location){
        return getInfo(LocationUtil.convertToSimpleLocation(location));
    }
}
