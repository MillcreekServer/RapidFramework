package io.github.wysohn.rapidframework2.manager;

import io.github.wysohn.rapidframework2.main.PluginMain;

public abstract class Manager {
    public static final int FASTEST_PRIORITY = 0;
    public static final int NORM_PRIORITY = 5;
    public static final int SLOWEST_PRIORITY = 10;

    private final PluginMain main;
    private final int loadPriority;

    public Manager(PluginMain main, int loadPriority) {
        this.main = main;
        this.loadPriority = loadPriority;
    }

    public abstract void enable() throws Exception;

    public abstract void load() throws Exception;

    public abstract void disable() throws Exception;
}
