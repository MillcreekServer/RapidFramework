package io.github.wysohn.rapidframework3.core.main;

import com.google.inject.Guice;
import com.google.inject.Module;
import io.github.wysohn.rapidframework3.core.inject.module.LoggerModule;
import io.github.wysohn.rapidframework3.core.inject.module.MainCommandsModule;
import io.github.wysohn.rapidframework3.core.inject.module.PluginDirectoryModule;
import io.github.wysohn.rapidframework3.core.inject.module.PluginInfoModule;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PluginMainBuilder {
    private final List<Module> moduleList = new LinkedList<>();

    public static PluginMainBuilder prepare(PluginInfoModule infoModule,
                                            MainCommandsModule commandModule,
                                            LoggerModule loggerModule,
                                            PluginDirectoryModule directoryModule) {
        PluginMainBuilder builder = new PluginMainBuilder();
        builder.moduleList.add(Objects.requireNonNull(infoModule));
        builder.moduleList.add(Objects.requireNonNull(commandModule));
        builder.moduleList.add(Objects.requireNonNull(loggerModule));
        builder.moduleList.add(Objects.requireNonNull(directoryModule));
        return builder;
    }

    public PluginMainBuilder addModule(Module module) {
        moduleList.add(module);
        return this;
    }

    public PluginMain build() {
        return Guice.createInjector(moduleList).getInstance(PluginMain.class);
    }
}
