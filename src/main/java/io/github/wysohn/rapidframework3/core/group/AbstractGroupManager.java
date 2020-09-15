package io.github.wysohn.rapidframework3.core.group;

import com.google.inject.Injector;
import io.github.wysohn.rapidframework3.core.caching.AbstractManagerElementCaching;
import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;

import java.util.UUID;
import java.util.function.Consumer;

public abstract class AbstractGroupManager<V extends Group> extends AbstractManagerElementCaching<UUID, V> {
    public AbstractGroupManager(PluginMain main, ISerializer serializer, Injector injector, Class<V> type) {
        super(main, serializer, injector, type);
    }

    @Override
    protected UUID fromString(String string) {
        return UUID.fromString(string);
    }

    public <T extends Group> void reset(T group, Consumer<T> doBefore) {
        doBefore.andThen(g -> {
            delete(g.getUuid());
            deCache(g.getUuid());
        }).accept(group);
    }
}
