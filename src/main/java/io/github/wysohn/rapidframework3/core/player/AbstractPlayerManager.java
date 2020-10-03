package io.github.wysohn.rapidframework3.core.player;

import com.google.inject.Injector;
import io.github.wysohn.rapidframework3.core.caching.AbstractManagerElementCaching;
import io.github.wysohn.rapidframework3.core.main.ManagerConfig;
import io.github.wysohn.rapidframework3.interfaces.plugin.IShutdownHandle;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework3.interfaces.serialize.ITypeAsserter;

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
                                 Injector injector, Class<V> type) {
        super(pluginName, logger, config, pluginDir, shutdownHandle, serializer, asserter, injector, type);
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
