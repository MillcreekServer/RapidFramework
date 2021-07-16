package io.github.wysohn.rapidframework3.core.group;

import com.google.inject.Injector;
import io.github.wysohn.rapidframework3.core.caching.AbstractManagerElementCaching;
import io.github.wysohn.rapidframework3.core.inject.factory.IDatabaseFactoryCreator;
import io.github.wysohn.rapidframework3.core.main.ManagerConfig;
import io.github.wysohn.rapidframework3.interfaces.plugin.IShutdownHandle;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework3.interfaces.serialize.ITypeAsserter;

import java.io.File;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

public abstract class AbstractGroupManager<V extends Group> extends AbstractManagerElementCaching<UUID, V> {
    public AbstractGroupManager(String pluginName,
                                Logger logger,
                                ManagerConfig config,
                                File pluginDir,
                                IShutdownHandle shutdownHandle,
                                ISerializer serializer,
                                ITypeAsserter asserter,
                                IDatabaseFactoryCreator factoryCreator,
                                Injector injector,
                                String tableName,
                                Class<V> type) {
        super(pluginName,
              logger,
              config,
              pluginDir,
              shutdownHandle,
              serializer,
              asserter,
              factoryCreator,
              injector,
              tableName,
              type);
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
