package io.github.wysohn.rapidframework.pluginbase.objects.structure.interfaces.trigger;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.objects.Area;
import io.github.wysohn.rapidframework.pluginbase.objects.structure.interfaces.filter.EntityFilter;

import org.bukkit.entity.Entity;

public interface RegionTrigger extends Trigger {
    Area getArea();

    void onEnter(PluginBase base, Entity entity, EntityFilter filter);
    void onExit(PluginBase base, Entity entity, EntityFilter filter);
}
