package io.github.wysohn.rapidframework4.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework4.interfaces.plugin.IGlobalPluginManager;

public class GlobalPluginManagerModule extends AbstractModule {
    private final IGlobalPluginManager globalPluginManager;

    public GlobalPluginManagerModule(IGlobalPluginManager globalPluginManager) {
        this.globalPluginManager = globalPluginManager;
    }

    @Provides
    IGlobalPluginManager getGlobalPluginManager() {
        return globalPluginManager;
    }
}
