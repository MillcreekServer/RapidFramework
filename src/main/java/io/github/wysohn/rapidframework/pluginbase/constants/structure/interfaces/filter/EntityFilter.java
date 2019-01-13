package io.github.wysohn.rapidframework.pluginbase.constants.structure.interfaces.filter;

import io.github.wysohn.rapidframework.pluginbase.constants.structure.Structure;
import org.bukkit.entity.Entity;

public interface EntityFilter<E extends Entity> {
    boolean isPermitted(Structure structure, E entity);
}
