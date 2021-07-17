package io.github.wysohn.rapidframework4.bukkit.inject.module;

import io.github.wysohn.rapidframework4.core.inject.module.PluginInfoModule;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;

public class BukkitPluginInfoModule extends PluginInfoModule {
    public BukkitPluginInfoModule(PluginDescriptionFile descriptionFile) {
        super(descriptionFile.getName(),
                descriptionFile.getFullName(),
                descriptionFile.getPermissions()
                        .stream()
                        .findFirst()
                        .map(Permission::getName)
                        .orElse(descriptionFile.getName().toLowerCase()));
    }
}
