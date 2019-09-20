package io.github.wysohn.rapidframework2.core.manager.common;

import io.github.wysohn.rapidframework2.core.interfaces.plugin.PluginRuntime;
import io.github.wysohn.rapidframework2.core.main.PluginMain;

public abstract class Manager implements PluginRuntime {
    public static final int FASTEST_PRIORITY = 0;
    private final int loadPriority;

    public Manager(PluginMain main, int loadPriority) {
        this.main = main;
        this.loadPriority = loadPriority;
    }

    public static final int NORM_PRIORITY = 5;
    public static final int SLOWEST_PRIORITY = 10;
    protected final PluginMain main;

    public int getLoadPriority() {
        return loadPriority;
    }
}
