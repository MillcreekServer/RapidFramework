package io.github.wysohn.rapidframework2.core.interfaces.plugin;

public interface PluginRuntime {
    default void preload() throws Exception{

    }

    void enable() throws Exception;

    void load() throws Exception;

    void disable() throws Exception;
}
