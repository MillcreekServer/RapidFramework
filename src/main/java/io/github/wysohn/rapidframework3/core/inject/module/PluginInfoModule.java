package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

public class PluginInfoModule extends AbstractModule {
    private final String pluginName;
    private final String description;
    private final String rootPermission;

    public PluginInfoModule(String pluginName, String description, String rootPermission) {
        this.pluginName = pluginName;
        this.description = description;
        this.rootPermission = rootPermission;
    }

    @Provides
    @Named("pluginName")
    public String getPluginName() {
        return pluginName;
    }

    @Provides
    @Named("description")
    public String getDescription() {
        return description;
    }

    @Provides
    @Named("rootPermission")
    public String getRootPermission() {
        return rootPermission;
    }
}
