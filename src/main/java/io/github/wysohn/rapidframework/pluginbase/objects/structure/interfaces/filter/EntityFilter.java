package io.github.wysohn.rapidframework.pluginbase.objects.structure.interfaces.filter;

import io.github.wysohn.rapidframework.pluginbase.objects.structure.Structure;
import org.bukkit.entity.Entity;

public interface EntityFilter<E extends Entity> {
    boolean isPermitted(Structure structure, E entity);
}
