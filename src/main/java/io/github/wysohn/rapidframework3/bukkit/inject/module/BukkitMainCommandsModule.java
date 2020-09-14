package io.github.wysohn.rapidframework3.bukkit.inject.module;

import io.github.wysohn.rapidframework3.core.inject.module.MainCommandsModule;
import org.bukkit.plugin.PluginDescriptionFile;

public class BukkitMainCommandsModule extends MainCommandsModule {
    public BukkitMainCommandsModule(PluginDescriptionFile descriptionFile) {
        super(descriptionFile.getCommands()
                .keySet()
                .toArray(new String[0]));
    }
}
