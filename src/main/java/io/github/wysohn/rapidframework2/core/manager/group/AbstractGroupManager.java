package io.github.wysohn.rapidframework2.core.manager.group;

import io.github.wysohn.rapidframework2.core.manager.caching.AbstractManagerElementCaching;

import java.util.UUID;

public abstract class AbstractGroupManager<V extends Group> extends AbstractManagerElementCaching<UUID, V> {
    public AbstractGroupManager(int loadPriority) {
        super(loadPriority);
    }

    @Override
    protected UUID fromString(String string) {
        return UUID.fromString(string);
    }
}
