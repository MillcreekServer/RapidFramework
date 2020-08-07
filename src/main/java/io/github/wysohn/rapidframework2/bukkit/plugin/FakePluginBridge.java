package io.github.wysohn.rapidframework2.bukkit.plugin;

import io.github.wysohn.rapidframework2.bukkit.main.AbstractBukkitPlugin;
import io.github.wysohn.rapidframework2.bukkit.main.BukkitPluginBridge;
import io.github.wysohn.rapidframework2.bukkit.manager.api.PlaceholderAPI;
import io.github.wysohn.rapidframework2.bukkit.plugin.manager.ManagerChat;
import io.github.wysohn.rapidframework2.bukkit.plugin.manager.TranslateManager;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.IPluginManager;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.command.SubCommand;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class FakePluginBridge extends BukkitPluginBridge {
    public FakePluginBridge(AbstractBukkitPlugin bukkit) {
        super(bukkit);
    }

    public FakePluginBridge(String pluginName, String pluginDescription, String mainCommand, String adminPermission, Logger logger, File dataFolder, IPluginManager iPluginManager, AbstractBukkitPlugin bukkit) {
        super(pluginName, pluginDescription, mainCommand, adminPermission, logger, dataFolder, iPluginManager, bukkit);
    }

    @Override
    protected PluginMain init(PluginMain.Builder builder) {
        return builder
                .withManagers(new TranslateManager(PluginMain.Manager.NORM_PRIORITY))
                .withManagers(new ManagerChat(PluginMain.Manager.NORM_PRIORITY,
                        builder.getPluginDirectory(),
                        (sender, str) -> getMain().api().getAPI(PlaceholderAPI.class)
                                .map(api -> api.parse(sender, str))
                                .orElse(str)))
                .build();
    }

    @Override
    protected void registerCommands(List<SubCommand> commands) {

    }
}