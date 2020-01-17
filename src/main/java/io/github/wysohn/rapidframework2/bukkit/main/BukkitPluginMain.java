package io.github.wysohn.rapidframework2.bukkit.main;

import io.github.wysohn.rapidframework.utils.files.FileUtil;
import io.github.wysohn.rapidframework2.bukkit.main.objects.BukkitCommandSender;
import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.command.SubCommand;
import io.github.wysohn.rapidframework2.core.manager.lang.LanguageSession;
import io.github.wysohn.rapidframework2.core.manager.lang.message.Message;
import io.github.wysohn.rapidframework2.core.manager.player.IPlayerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public abstract class BukkitPluginMain extends JavaPlugin {
    protected final ExecutorService asyncTaskExecutor = Executors.newCachedThreadPool((runnable) -> {
        Thread thread = new Thread(runnable);
        thread.setPriority(Thread.MIN_PRIORITY);
        return thread;
    });

    protected final BukkitPluginBridge bukkitPluginBridge = new BukkitPluginBridge(this);

    protected final PluginMain.Builder mainBuilder = PluginMain.Builder.prepare(
            getDescription().getName(),
            getDescription().getDescription(),
            getDescription().getCommands().entrySet().stream().findFirst().get().getKey(),
            getDescription().getPermissions().stream().findFirst().get().getName(),
            bukkitPluginBridge,
            getLogger(),
            getDataFolder())
            .andConfigSession(new ConfigFileSession(FileUtil.join(getDataFolder(), "config.yml")))
            .andPluginSupervisor(pluginName -> Bukkit.getPluginManager().isPluginEnabled(pluginName))
            .andLanguageSessionFactory(locale -> new LanguageSession(new ConfigFileSession(FileUtil.join(getDataFolder(),
                    "lang", locale.getLanguage() + ".yml"))))
            .setMessageSender(((sender, message) -> Arrays.stream(message)
                    .map(Message::getString)
                    .reduce((a, b) -> a + " " + b)
                    .map(combined -> ChatColor.translateAlternateColorCodes('&', combined))
                    .ifPresent(combined -> {
                        sender.sendMessageRaw(combined);
                        Optional.ofNullable(message[0].getHover_ShowText())
                                .map(text -> text.split("\n"))
                                .map(Stream::of)
                                .ifPresent(stringStream -> stringStream
                                        .map(hover -> ChatColor.translateAlternateColorCodes('&', hover))
                                        .forEach(hover -> sender.sendMessageRaw("  " + hover)));
                    })));

    private PluginMain main;

    protected abstract PluginMain init(List<SubCommand> commands);

    @Override
    public void onEnable() {
        super.onEnable();

        try {
            List<SubCommand> commands = new ArrayList<>();
            main = init(commands);

            main.enable();

            getCommand(getDescription().getCommands().entrySet().stream().findFirst().get().getKey()).setTabCompleter(this);
            commands.forEach(main.comm()::addCommand);
            main.getOrderedManagers().stream()
                    .filter(Listener.class::isInstance)
                    .map(Listener.class::cast)
                    .forEach(manager -> Bukkit.getPluginManager().registerEvents(manager, this));

            main.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        try {
            main.disable();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return main.comm().onCommand(Optional.of(sender)
                        .filter(Player.class::isInstance)
                        .map(Player.class::cast)
                        .flatMap(this::getPlayerWrapper)
                        .map(ICommandSender.class::cast)
                        .orElseGet(() -> new BukkitCommandSender<>().setSender(sender)),
                command.getName(),
                label,
                args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return main.comm().onTabComplete(Optional.of(sender)
                        .filter(Player.class::isInstance)
                        .map(Player.class::cast)
                        .flatMap(this::getPlayerWrapper)
                        .map(ICommandSender.class::cast)
                        .orElseGet(() -> new BukkitCommandSender<>().setSender(sender)),
                command.getName(),
                label,
                args);
    }

    protected Optional<? extends IPlayerWrapper> getPlayerWrapper(Player player) {
        return Optional.ofNullable(player)
                .map(Entity::getUniqueId)
                .flatMap(this::getPlayerWrapper);
    }

    protected abstract Optional<? extends IPlayerWrapper> getPlayerWrapper(UUID uuid);
}
