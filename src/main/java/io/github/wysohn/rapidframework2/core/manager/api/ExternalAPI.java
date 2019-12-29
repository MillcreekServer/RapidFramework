package io.github.wysohn.rapidframework2.core.manager.api;

import io.github.wysohn.rapidframework2.core.interfaces.plugin.PluginRuntime;
import io.github.wysohn.rapidframework2.core.main.PluginMain;

public abstract class ExternalAPI implements PluginRuntime {
    protected final PluginMain main;
    protected final String pluginName;

    public ExternalAPI(PluginMain main, String pluginName) {
        this.main = main;
        this.pluginName = pluginName;
    }

    public String getPluginName() {
        return pluginName;
    }
}