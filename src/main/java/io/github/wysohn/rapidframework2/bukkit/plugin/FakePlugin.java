package io.github.wysohn.rapidframework2.bukkit.plugin;

import io.github.wysohn.rapidframework2.bukkit.main.AbstractBukkitPlugin;
import io.github.wysohn.rapidframework2.bukkit.main.BukkitPluginBridge;
import io.github.wysohn.rapidframework2.bukkit.main.objects.BukkitPlayer;
import io.github.wysohn.rapidframework2.bukkit.main.objects.BukkitWrapper;
import io.github.wysohn.rapidframework2.bukkit.manager.common.message.BukkitMessageBuilder;
import io.github.wysohn.rapidframework2.bukkit.utils.conversation.ConversationBuilder;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.IPluginManager;
import io.github.wysohn.rapidframework2.core.manager.player.AbstractPlayerWrapper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class FakePlugin extends AbstractBukkitPlugin {
    @Override
    protected BukkitPluginBridge createCore() {
        return new FakePluginBridge(this);
    }

    @Override
    protected BukkitPluginBridge createCore(String pluginName,
                                            String pluginDescription,
                                            String mainCommand,
                                            String adminPermission,
                                            Logger logger,
                                            File dataFolder,
                                            IPluginManager iPluginManager) {
        return new FakePluginBridge(pluginName, pluginDescription, mainCommand, adminPermission,
                logger, dataFolder, iPluginManager, this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (!sender.hasPermission("rapidframework.admin"))
            return true;

        if (args.length > 0 && "test".equalsIgnoreCase(args[0])) {
            if (args.length > 1 && "jsonitem".equalsIgnoreCase(args[1])) {
                BukkitPlayer player = (BukkitPlayer) BukkitWrapper.player((Player) sender);
                getMain().lang().sendRawMessage(player, BukkitMessageBuilder.forBukkitMessage("test item:")
                        .append(" ")
                        .append("[item]")
                        .withHoverShowItem(player.getSender().getInventory().getItemInMainHand())
                        .build());

                return true;
            }

            if (args.length > 1 && "conv".equalsIgnoreCase(args[1])) {
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
}
