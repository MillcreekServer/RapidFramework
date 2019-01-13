package io.github.wysohn.rapidframework.pluginbase.constants.structure.interfaces.trigger;

import org.bukkit.entity.Entity;

import java.lang.ref.WeakReference;

public interface EntityTrackingRegionTrigger extends RegionTrigger {
    void addEntity(WeakReference<Entity> entity);
    void removeEntity(WeakReference<Entity> entity);
}
