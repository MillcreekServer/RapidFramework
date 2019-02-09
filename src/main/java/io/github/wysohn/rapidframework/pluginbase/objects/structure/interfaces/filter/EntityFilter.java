package io.github.wysohn.rapidframework.pluginbase.objects.structure.interfaces.filter;

import org.bukkit.entity.Entity;

import io.github.wysohn.rapidframework.pluginbase.objects.structure.Structure;

public interface EntityFilter<E extends Entity> {
    boolean isPermitted(Structure structure, E entity);
}
