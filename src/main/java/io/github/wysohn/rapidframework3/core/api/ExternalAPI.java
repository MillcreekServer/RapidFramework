package io.github.wysohn.rapidframework3.core.api;

import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.interfaces.plugin.PluginRuntime;

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