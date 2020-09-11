package io.github.wysohn.rapidframework3.core.main;

import io.github.wysohn.rapidframework3.core.interfaces.plugn.PluginRuntime;

public abstract class PluginModule implements PluginRuntime {
    private final PluginMain main;

    public PluginModule(PluginMain main) {
        this.main = main;
    }

    public PluginMain main() {
        return main;
    }
}