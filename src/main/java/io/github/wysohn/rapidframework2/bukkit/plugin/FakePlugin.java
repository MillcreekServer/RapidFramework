package io.github.wysohn.rapidframework2.bukkit.plugin;

import io.github.wysohn.rapidframework2.bukkit.main.AbstractBukkitPlugin;
import io.github.wysohn.rapidframework2.bukkit.main.BukkitPluginBridge;
import io.github.wysohn.rapidframework2.bukkit.plugin.manager.TranslateManager;
import io.github.wysohn.rapidframework2.bukkit.utils.conversation.ConversationBuilder;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.IPluginManager;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.command.SubCommand;
import io.github.wysohn.rapidframework2.core.manager.player.AbstractPlayerWrapper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class FakePlugin extends AbstractBukkitPlugin {
    @Override
    protected BukkitPluginBridge createCore() {
        return new Bridge(this);
    }

    @Override
    protected BukkitPluginBridge createCore(String pluginName,
                                            String pluginDescription,
                                            String mainCommand,
                                            String adminPermission,
                                            Logger logger,
                                            File dataFolder,
                                            IPluginManager iPluginManager) {
        return new Bridge(pluginName, pluginDescription, mainCommand, adminPermission,
                logger, dataFolder, iPluginManager, this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (args.length > 0 && "test".equalsIgnoreCase(args[0])) {
            if (args.length > 1 && "conv".equalsIgnoreCase(args[1])) {
                if (!sender.hasPermission("rapidframework.admin"))
                    return true;

                ConversationBuilder.of(getMain())
                        .doTask(context -> {
                            context.setSessionData("Confirmed", false);
                        })
                        .appendInt((context, integer) -> {
                            context.setSessionData("Integer", integer);
                            return true;
                        })
                        .appendDouble((context, aDouble) -> {
                            context.setSessionData("Double", aDouble);
                            return true;
                        })
                        .appendConfirm((context) -> {
                            context.setSessionData("Confirmed", true);
                        })
                        .doTask(context -> {
                            sender.sendMessage("Integer: " + context.getSessionData("Integer"));
                            sender.sendMessage("Double: " + context.getSessionData("Double"));
                            sender.sendMessage("Confirmed: " + context.getSessionData("Confirmed"));
                        })
                        .build((Conversable) sender)
                        .begin();

                return true;
            }
        }

        return super.onCommand(sender, command, label, args);
    }

    @Override
    protected Optional<? extends AbstractPlayerWrapper> getPlayerWrapper(UUID uuid) {
        return Optional.empty();
    }

    private class Bridge extends BukkitPluginBridge {
        public Bridge(AbstractBukkitPlugin bukkit) {
            super(bukkit);
        }

        public Bridge(String pluginName, String pluginDescription, String mainCommand, String adminPermission, Logger logger, File dataFolder, IPluginManager iPluginManager, AbstractBukkitPlugin bukkit) {
            super(pluginName, pluginDescription, mainCommand, adminPermission, logger, dataFolder, iPluginManager, bukkit);
        }

        @Override
        protected PluginMain init(PluginMain.Builder builder) {
            return builder
                    .withManagers(new TranslateManager(PluginMain.Manager.NORM_PRIORITY))
                    .build();
        }

        @Override
        protected void registerCommands(List<SubCommand> commands) {

        }
    }
}
