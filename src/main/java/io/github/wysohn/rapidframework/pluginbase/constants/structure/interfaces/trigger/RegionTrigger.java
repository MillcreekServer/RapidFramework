package io.github.wysohn.rapidframework.pluginbase.constants.structure.interfaces.trigger;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.constants.Area;
import io.github.wysohn.rapidframework.pluginbase.constants.structure.interfaces.filter.EntityFilter;
import org.bukkit.entity.Entity;

public interface RegionTrigger extends Trigger {
    Area getArea();

    void onEnter(PluginBase base, Entity entity, EntityFilter filter);
    void onExit(PluginBase base, Entity entity, EntityFilter filter);
}
