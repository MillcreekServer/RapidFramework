package io.github.wysohn.rapidframework3.core.main;

import io.github.wysohn.rapidframework3.interfaces.plugin.PluginRuntime;
import io.github.wysohn.rapidframework3.utils.Validation;

public abstract class PluginModule implements PluginRuntime {
    private final PluginMain main;

    public PluginModule(PluginMain main) {
        this.main = main;
    }

    public PluginMain main() {
        return main;
    }

    protected static void verifySingleton(PluginModule module) {
        Validation.validate(module,
                m -> m.getClass().getAnnotation(com.google.inject.Singleton.class) != null
                        || m.getClass().getAnnotation(javax.inject.Singleton.class) != null,
                String.format("%s is not annotated with Singleton!", module.getClass()));
    }
}