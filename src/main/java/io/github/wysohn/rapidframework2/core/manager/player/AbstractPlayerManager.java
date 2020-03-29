package io.github.wysohn.rapidframework2.core.manager.player;

import io.github.wysohn.rapidframework2.core.manager.caching.AbstractManagerElementCaching;

import java.util.UUID;

public abstract class AbstractPlayerManager<V extends AbstractPlayerWrapper> extends AbstractManagerElementCaching<UUID, V> {
    public AbstractPlayerManager(int loadPriority) {
        super(loadPriority);
    }

    protected abstract IConstructionHandle<UUID, V> createHandle();

    @Override
    public void enable() throws Exception {
        super.enable();

        setConstructionHandle(createHandle());
    }

    @Override
    protected UUID fromString(String string) {
        return UUID.fromString(string);
    }
}
