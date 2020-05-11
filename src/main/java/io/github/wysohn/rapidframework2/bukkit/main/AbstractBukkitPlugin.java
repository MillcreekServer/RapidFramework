package io.github.wysohn.rapidframework2.bukkit.main;

import io.github.wysohn.rapidframework2.bukkit.main.objects.BukkitCommandSender;
import io.github.wysohn.rapidframework2.bukkit.main.objects.BukkitPlayer;
import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.IPluginManager;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.player.AbstractPlayerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Logger;

public abstract class AbstractBukkitPlugin extends JavaPlugin {
    private static final ExecutorService asyncTaskExecutor = Executors.newCachedThreadPool((runnable)->{
        Thread thread = new Thread(runnable);
        thread.setPriority(Thread.MAX_PRIORITY);
        return thread;
    });

    protected BukkitPluginBridge core;

    public PluginMain getMain() {
        return core.getMain();
    }

    protected abstract BukkitPluginBridge createCore();

    protected abstract BukkitPluginBridge createCore(String pluginName,
                                                     String pluginDescription,
                                                     String mainCommand,
                                                     String adminPermission,
                                                     Logger logger,
                                                     File dataFolder,
                                                     IPluginManager iPluginManager);

    public void setEnableState(boolean bool){
        this.setEnabled(bool);
    }

    public PluginManager getPluginManager(){
        return Bukkit.getPluginManager();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        core = createCore();
        core.init();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        core.enable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        core.disable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return core.onCommand(Optional.of(sender)
                        .filter(Player.class::isInstance)
                        .map(Player.class::cast)
                        .flatMap(this::getPlayerWrapper)
                        .map(ICommandSender.class::cast)
                        .orElseGet(() -> new BukkitCommandSender().setSender(sender)),
                command.getName(),
                label,
                args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return core.onTabComplete(Optional.of(sender)
                        .filter(Player.class::isInstance)
                        .map(Player.class::cast)
                        .flatMap(this::getPlayerWrapper)
                        .map(ICommandSender.class::cast)
                        .orElseGet(() -> new BukkitCommandSender().setSender(sender)),
                command.getName(),
                label,
                args);
    }

    public void forEachSender(Consumer<ICommandSender> consumer) {
        Bukkit.getOnlinePlayers().stream()
                .map(player -> new BukkitPlayer(player.getUniqueId()).setSender(player))
                .forEach(consumer);
    }

    protected Optional<? extends AbstractPlayerWrapper> getPlayerWrapper(Player player) {
        return Optional.ofNullable(player)
                .map(Entity::getUniqueId)
                .flatMap(this::getPlayerWrapper);
    }

    protected abstract Optional<? extends AbstractPlayerWrapper> getPlayerWrapper(UUID uuid);
}
