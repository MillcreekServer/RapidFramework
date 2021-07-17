package io.github.wysohn.rapidframework4.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework4.core.inject.annotations.PluginCommands;

public class MainCommandsModule extends AbstractModule {
    private final String[] mainCommands;

    public MainCommandsModule(String... mainCommands) {
        this.mainCommands = mainCommands;
        if (mainCommands.length < 1)
            throw new RuntimeException("At least one main command must be provided.");
    }

    @Provides
    @PluginCommands
    String[] getMainCommands() {
        return mainCommands;
    }
}
