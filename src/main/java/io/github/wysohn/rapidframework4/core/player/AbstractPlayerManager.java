package io.github.wysohn.rapidframework4.core.player;

import com.google.inject.Injector;
import io.github.wysohn.rapidframework4.core.caching.AbstractManagerElementCaching;
import io.github.wysohn.rapidframework4.core.inject.factory.IDatabaseFactoryCreator;
import io.github.wysohn.rapidframework4.core.main.ManagerConfig;
import io.github.wysohn.rapidframework4.interfaces.plugin.IShutdownHandle;
import io.github.wysohn.rapidframework4.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework4.interfaces.serialize.ITypeAsserter;

import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;

public abstract class AbstractPlayerManager<V extends AbstractPlayerWrapper> extends AbstractManagerElementCaching<UUID, V> {
    public AbstractPlayerManager(String pluginName,
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
    public void enable() throws Exception {
        super.enable();
    }

    @Override
    protected UUID fromString(String string) {
        return UUID.fromString(string);
    }
}
