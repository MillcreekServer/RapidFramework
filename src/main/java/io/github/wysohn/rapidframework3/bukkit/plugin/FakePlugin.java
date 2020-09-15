package io.github.wysohn.rapidframework3.bukkit.plugin;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.bukkit.data.BukkitPlayer;
import io.github.wysohn.rapidframework3.bukkit.data.BukkitWrapper;
import io.github.wysohn.rapidframework3.bukkit.main.AbstractBukkitPlugin;
import io.github.wysohn.rapidframework3.bukkit.manager.api.PlaceholderAPI;
import io.github.wysohn.rapidframework3.bukkit.manager.common.message.BukkitMessageBuilder;
import io.github.wysohn.rapidframework3.bukkit.plugin.manager.ManagerChat;
import io.github.wysohn.rapidframework3.bukkit.plugin.manager.TranslateManager;
import io.github.wysohn.rapidframework3.bukkit.utils.conversation.ConversationBuilder;
import io.github.wysohn.rapidframework3.core.api.ManagerExternalAPI;
import io.github.wysohn.rapidframework3.core.command.SubCommand;
import io.github.wysohn.rapidframework3.core.inject.module.LanguagesModule;
import io.github.wysohn.rapidframework3.core.inject.module.ManagerModule;
import io.github.wysohn.rapidframework3.core.main.PluginMainBuilder;
import io.github.wysohn.rapidframework3.core.player.AbstractPlayerWrapper;
import io.github.wysohn.rapidframework3.interfaces.chat.IPlaceholderSupport;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakePlugin extends AbstractBukkitPlugin {
    public FakePlugin() {
    }

    FakePlugin(@NotNull JavaPluginLoader mockLoader) {
        super(mockLoader);
    }

    @Override
    protected void init(PluginMainBuilder builder) {
        builder.addModule(new ManagerModule(TranslateManager.class,
                ManagerChat.class));
        builder.addModule(new LanguagesModule());
        builder.addModule(new AbstractModule() {
            @Provides
            IPlaceholderSupport placeholderSupport(ManagerExternalAPI manager) {
                return (sender, str) -> manager.getAPI(PlaceholderAPI.class)
                        .map(api -> api.parse(sender, str))
                        .orElse(str);
            }
        });
    }

    @Override
    protected void registerCommands(List<SubCommand> commands) {

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
