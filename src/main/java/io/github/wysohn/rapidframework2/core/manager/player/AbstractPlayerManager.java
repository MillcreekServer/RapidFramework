package io.github.wysohn.rapidframework2.core.manager.player;

import io.github.wysohn.rapidframework2.core.manager.caching.AbstractManagerElementCaching;

import java.util.UUID;

public abstract class AbstractPlayerManager<V extends AbstractPlayerWrapper> extends AbstractManagerElementCaching<UUID, V> {
    public AbstractPlayerManager(int loadPriority, IConstructionHandle<UUID, V> constructionHandle) {
        super(loadPriority);
        this.setConstructionHandle(constructionHandle);
    }

    @Override
    protected UUID fromString(String string) {
        return UUID.fromString(string);
    }
}
