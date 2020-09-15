package io.github.wysohn.rapidframework3.core.main;

import com.google.inject.Singleton;
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

    protected static void verifySingleton(Manager manager) {
        Validation.validate(manager,
                m -> m.getClass().getAnnotation(Singleton.class) != null,
                String.format("Manager %s is not annotated with com.google.inject.Singleton!", manager.getClass()));
    }
}