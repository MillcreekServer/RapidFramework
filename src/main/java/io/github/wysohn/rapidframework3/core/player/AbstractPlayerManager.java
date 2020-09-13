package io.github.wysohn.rapidframework3.core.player;

import com.google.inject.Injector;
import io.github.wysohn.rapidframework3.core.caching.AbstractManagerElementCaching;
import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;

import java.util.UUID;

public abstract class AbstractPlayerManager<V extends AbstractPlayerWrapper> extends AbstractManagerElementCaching<UUID, V> {
    public AbstractPlayerManager(PluginMain main, ISerializer serializer, Injector injector, Class<V> type) {
        super(main, serializer, injector, type);
    }

    @Override
    public void enable() throws Exception {
        super.enable();
    }

    @Override
    protected UUID fromString(String string) {
        return UUID.fromString(string);
    }
}
