package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.objects.ClaimInfo;
import io.github.wysohn.rapidframework.pluginbase.objects.SimpleLocation;
import io.github.wysohn.rapidframework.utils.locations.LocationUtil;
import org.bukkit.Location;

import java.util.Set;

public abstract class DefaultHandle<PB extends PluginBase> {
    private final AbstractManagerRegion<PB, ? extends ClaimInfo> rmanager;

    protected DefaultHandle(AbstractManagerRegion rmanager) {
        this.rmanager = rmanager;
    }

    /**
     * @param simpleLocation
     * @return
     * @deprecated
     */
    @Deprecated
    protected ClaimInfo getInfo(SimpleLocation simpleLocation) {
        return rmanager.getAreaInfo(simpleLocation).stream().findFirst().orElse(null);
    }

    /**
     * @param simpleLocation
     * @return
     * @deprecated
     */
    @Deprecated
    protected ClaimInfo getInfo(Location location) {
        return getInfo(LocationUtil.convertToSimpleLocation(location));
    }

    protected Set<? extends ClaimInfo> getInfos(SimpleLocation simpleLocation) {
        return rmanager.getAreaInfo(simpleLocation);
    }

    protected Set<? extends ClaimInfo> getInfos(Location location) {
        return getInfos(LocationUtil.convertToSimpleLocation(location));
    }
}
