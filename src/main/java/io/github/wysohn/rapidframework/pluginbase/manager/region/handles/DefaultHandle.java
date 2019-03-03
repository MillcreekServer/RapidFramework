package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.objects.ClaimInfo;
import io.github.wysohn.rapidframework.pluginbase.objects.SimpleLocation;
import io.github.wysohn.rapidframework.utils.locations.LocationUtil;
import org.bukkit.Location;

public abstract class DefaultHandle {
    private final AbstractManagerRegion rmanager;

    protected DefaultHandle(AbstractManagerRegion rmanager) {
	this.rmanager = rmanager;
    }

    protected ClaimInfo getInfo(SimpleLocation simpleLocation) {
	return rmanager.getAreaInfo(simpleLocation);
    }

    protected ClaimInfo getInfo(Location location) {
	return getInfo(LocationUtil.convertToSimpleLocation(location));
    }
}
