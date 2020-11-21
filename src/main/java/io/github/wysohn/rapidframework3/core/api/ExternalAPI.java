package io.github.wysohn.rapidframework3.core.api;

import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.interfaces.plugin.PluginRuntime;

/**
 * Parent class for any manager that handles external APIs.
 *
 * This class will be dynamically instantiated when the target
 * plugin, which contains the API you are trying to use, exist
 * to avoid NoClassDefFoundError. Such error can be avoided
 * by not instantiating the class until the target plugin is found.
 *
 * Child class must have public constructor with PluginMain and String
 * as arguments, and those arguments will be filled-in runtime
 * when the target plugin is found.
 *
 * Plus, this class support dependency injection upon instantiation,
 * so you may choose to annotate the constructor or field with
 * {@link com.google.inject.Inject} or {@link javax.inject.Inject}
 * so that dependent classes can be automatically injected runtime.
 */
public abstract class ExternalAPI implements PluginRuntime {
    protected final PluginMain main;
    protected final String pluginName;

    /**
     *
     * @param main plugin main for your plugin
     * @param pluginName name of the hooked plugin. This will
     *                   match exactly with the 'name' of the
     *                   plugin.yml, definition of the target
     *                   plugin.
     */
    public ExternalAPI(PluginMain main, String pluginName) {
        this.main = main;
        this.pluginName = pluginName;
    }

    public String getPluginName() {
        return pluginName;
    }
}