package io.github.wysohn.rapidframework3.core.api;

import io.github.wysohn.rapidframework3.core.inject.annotations.ExternalPluginName;
import io.github.wysohn.rapidframework3.core.interfaces.plugn.PluginRuntime;
import io.github.wysohn.rapidframework3.core.main.PluginMain;

import javax.inject.Inject;

public abstract class ExternalAPI implements PluginRuntime {
    protected final PluginMain main;
    protected final String pluginName;

    @Inject
    public ExternalAPI(PluginMain main, @ExternalPluginName String pluginName) {
        this.main = main;
        this.pluginName = pluginName;
    }

    public String getPluginName() {
        return pluginName;
    }
}