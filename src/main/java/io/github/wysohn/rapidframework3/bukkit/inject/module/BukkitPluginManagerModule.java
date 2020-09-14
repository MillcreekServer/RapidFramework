package io.github.wysohn.rapidframework3.bukkit.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.interfaces.plugin.IGlobalPluginManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class BukkitPluginManagerModule extends AbstractModule {

    @Provides
    PluginManager getPluginManager() {
        return Bukkit.getPluginManager();
    }

    @Provides
    IGlobalPluginManager getGlobalPluginManager(PluginManager pluginManager) {
        return pluginManager::isPluginEnabled;
    }
}
