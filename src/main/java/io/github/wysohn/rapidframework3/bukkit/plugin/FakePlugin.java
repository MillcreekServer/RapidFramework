package io.github.wysohn.rapidframework3.bukkit.plugin;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import copy.com.google.gson.Gson;
import copy.com.google.gson.GsonBuilder;
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
import io.github.wysohn.rapidframework3.core.serialize.BukkitConfigurationSerializer;
import io.github.wysohn.rapidframework3.interfaces.chat.IPlaceholderSupport;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakePlugin extends AbstractBukkitPlugin {
    private static final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new BukkitConfigurationSerializer())
            .create();

    public FakePlugin() {
    }

    FakePlugin(@NotNull Server mockServer) {
        super(mockServer);
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
    protected void registerCommands(List<SubCommand.Builder> commands) {

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (!sender.hasPermission("rapidframework.admin"))
            return true;

        if (args.length > 0 && "test".equalsIgnoreCase(args[0])) {
            if(args.length > 1 && "similar".equalsIgnoreCase(args[1])){
                Player player = (Player) sender;
                ItemStack itemStack0 = player.getInventory().getItem(0);
                ItemStack itemStack1 = player.getInventory().getItem(1);
                sender.sendMessage("Similar: "+itemStack0.isSimilar(itemStack1));
                sender.sendMessage("item0: "+itemStack0);
                sender.sendMessage("item1: "+itemStack1);

                return true;
            }

            if(args.length > 1 && "serialize".equalsIgnoreCase(args[1])){
                Player player = (Player) sender;
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                // reconstruct item on hand using serialization
                // String ser = gson.toJson(itemStack, ItemStack.class);
                getConfig().set("test", itemStack);
                String ser = getConfig().saveToString();
                YamlConfiguration temp = new YamlConfiguration();
                try {
                    temp.loadFromString(ser);
                } catch (InvalidConfigurationException e) {
                    e.printStackTrace();
                }
                // ItemStack reconstructed = gson.fromJson(ser, ItemStack.class);
                ItemStack reconstructed = temp.getItemStack("test");
                player.getInventory().setItemInMainHand(reconstructed);
                sender.sendMessage("Reconstructed on hand");

                return true;
            }

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
