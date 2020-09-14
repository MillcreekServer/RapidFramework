package io.github.wysohn.rapidframework3.bukkit.inject.module;

import io.github.wysohn.rapidframework3.core.inject.module.PluginInfoModule;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;

public class BukkitPluginInfoModule extends PluginInfoModule {
    public BukkitPluginInfoModule(PluginDescriptionFile descriptionFile) {
        super(descriptionFile.getName(),
                descriptionFile.getDescription(),
                descriptionFile.getPermissions()
                        .stream()
                        .findFirst()
                        .map(Permission::getName)
                        .orElse(descriptionFile.getName().toLowerCase()));
    }
}
