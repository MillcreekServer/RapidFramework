package io.github.wysohn.rapidframework3.core.database;

import com.google.inject.Inject;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;
import io.github.wysohn.rapidframework3.core.inject.factory.IDatabaseFactoryCreator;
import io.github.wysohn.rapidframework3.core.main.ManagerConfig;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;

import javax.inject.Named;
import java.io.File;

public class DatabaseFactoryCreator implements IDatabaseFactoryCreator {
    private final String pluginName;
    private final File pluginDir;
    private final ISerializer serializer;
    private final ManagerConfig config;

    @Inject
    public DatabaseFactoryCreator(@Named("pluginName") String pluginName,
                                  @PluginDirectory File pluginDir,
                                  ISerializer serializer,
                                  ManagerConfig config) {
        this.pluginName = pluginName;
        this.pluginDir = pluginDir;
        this.serializer = serializer;
        this.config = config;
    }

    @Override
    public IDatabaseFactory create(String typeName) {
        return new DatabaseFactory(pluginName,
                                   pluginDir,
                                   serializer,
                                   config,
                                   typeName);
    }
}
