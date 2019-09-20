package io.github.wysohn.rapidframework2.core.interfaces.plugin;

public interface PluginRuntime {
    public abstract void enable() throws Exception;

    public abstract void load() throws Exception;

    public abstract void disable() throws Exception;
}
