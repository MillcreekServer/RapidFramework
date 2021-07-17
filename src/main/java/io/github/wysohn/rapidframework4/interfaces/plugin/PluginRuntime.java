package io.github.wysohn.rapidframework4.interfaces.plugin;

public interface PluginRuntime {
    default void preload() throws Exception {

    }

    void enable() throws Exception;

    void load() throws Exception;

    void disable() throws Exception;
}
