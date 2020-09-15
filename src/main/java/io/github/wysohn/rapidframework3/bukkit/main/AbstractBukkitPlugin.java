package io.github.wysohn.rapidframework3.bukkit.main;

import io.github.wysohn.rapidframework3.bukkit.data.BukkitWrapper;
import io.github.wysohn.rapidframework3.bukkit.inject.module.*;
import io.github.wysohn.rapidframework3.bukkit.manager.api.PlaceholderAPI;
import io.github.wysohn.rapidframework3.bukkit.manager.api.ProtocolLibAPI;
import io.github.wysohn.rapidframework3.core.command.SubCommand;
import io.github.wysohn.rapidframework3.core.inject.module.*;
import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.core.main.PluginMainBuilder;
import io.github.wysohn.rapidframework3.core.player.AbstractPlayerWrapper;
import io.github.wysohn.rapidframework3.interfaces.ICommandSender;
import io.github.wysohn.rapidframework3.interfaces.plugin.ITaskSupervisor;
import io.github.wysohn.rapidframework3.utils.JarUtil;
import io.github.wysohn.rapidframework3.utils.Pair;
import io.github.wysohn.rapidframework3.utils.Validation;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public abstract class AbstractBukkitPlugin extends JavaPlugin {
    private final ExecutorService executor = Executors.newCachedThreadPool(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("AsyncTask - " + AbstractBukkitPlugin.this.getClass().getSimpleName());
        thread.setPriority(Thread.NORM_PRIORITY - 1);
        return thread;
    });

    private static String nmsVersion;

    public static String getNmsVersion() {
        return nmsVersion;
    }

    /**
     * @param className NMS class name without packages (Just ItemStack, not net.minecraft.server...ItemStack)
     * @return NMS class correspond to current server's version.
     */
    public static Class<?> getNMSClass(String className) throws ClassNotFoundException {
        Validation.assertNotNull(className);
        return Class.forName("net.minecraft.server." + nmsVersion + "." + className);
    }

    private boolean test;
    private PluginMain main;

    public AbstractBukkitPlugin() {
    }

    /**
     * @param mockLoader
     * @deprecated for test only
     */
    protected AbstractBukkitPlugin(@NotNull JavaPluginLoader mockLoader) {
        super(mockLoader,
                new PluginDescriptionFile("test", "test", "test"),
                new File("build/tmp/tests/"),
                new File("build/tmp/tests/other"));
        test = true;
    }

    protected void copyConfigFiles(File dataFolder) throws IOException {
        JarUtil.copyFromJar(getClass(), "config.yml", dataFolder, JarUtil.CopyOption.COPY_IF_NOT_EXIST);
        JarUtil.copyFromJar("config.yml", dataFolder, JarUtil.CopyOption.COPY_IF_NOT_EXIST);
    }

    @Override
    public void onLoad() {
        String packageName = getServer().getClass().getPackage().getName();
        nmsVersion = packageName.substring(packageName.lastIndexOf('.') + 1);

        try {
            copyConfigFiles(getDataFolder());
        } catch (IOException e) {
            e.printStackTrace();
        }

        PluginMainBuilder builder = PluginMainBuilder.prepare(new BukkitPluginInfoModule(getDescription()),
                test ? new MainCommandsModule("test") : new BukkitMainCommandsModule(getDescription()),
                new LoggerModule(getLogger()),
                new PluginDirectoryModule(getDataFolder()));
        builder.addModule(new PlatformModule(this));
        builder.addModule(new DefaultManagersModule());
        builder.addModule(new MediatorModule());
        builder.addModule(new FileIOModule());
        builder.addModule(new BukkitStorageFactoryModule());
        builder.addModule(new BukkitBroadcasterModule());
        builder.addModule(test ? new GlobalPluginManagerModule(pluginName -> true) : new BukkitPluginManagerModule());
        builder.addModule(new TaskSupervisorModule(new ITaskSupervisor() {
            @Override
            public <V> Future<V> sync(Callable<V> callable) {
                return Bukkit.getScheduler().callSyncMethod(AbstractBukkitPlugin.this, callable);
            }

            @Override
            public void sync(Runnable runnable) {
                Bukkit.getScheduler().runTask(AbstractBukkitPlugin.this, runnable);
            }

            @Override
            public <V> Future<V> async(Callable<V> callable) {
                return executor.submit(callable);
            }

            @Override
            public void async(Runnable runnable) {
                async(() -> {
                    runnable.run();
                    return null;
                });
            }
        }));
        builder.addModule(new ExternalAPIModule(
                Pair.of("ProtocolLib", ProtocolLibAPI.class),
                Pair.of("PlaceholderAPI", PlaceholderAPI.class)
        ));
        builder.addModule(new BukkitMessageSenderModule());
        init(builder);
        this.main = builder.build();

        try {
            this.main.preload();
        } catch (Exception ex) {
            ex.printStackTrace();
            setEnableState(false);
        }
    }

    protected abstract void init(PluginMainBuilder builder);

    protected abstract void registerCommands(List<SubCommand> commands);

    @Override
    public void onEnable() {
        try {
            this.main.enable();

            getDescription().getCommands().keySet().stream()
                    .map(this::getCommand)
                    .filter(Objects::nonNull)
                    .forEach(pluginCommand -> pluginCommand.setTabCompleter(this));

            main.getOrderedManagers().stream()
                    .filter(Listener.class::isInstance)
                    .map(Listener.class::cast)
                    .forEach(manager -> Optional.of(Bukkit.getPluginManager())
                            .ifPresent(pluginManager -> pluginManager.registerEvents(manager, this)));

            main.getMediators().stream()
                    .filter(Listener.class::isInstance)
                    .map(Listener.class::cast)
                    .forEach(mediator -> Optional.of(Bukkit.getPluginManager())
                            .ifPresent(pluginManager -> pluginManager.registerEvents(mediator, this)));

            this.main.load();


            List<SubCommand> commands = new ArrayList<>();
            registerCommands(commands);
            commands.forEach(main.comm()::addCommand);
        } catch (Exception ex) {
            ex.printStackTrace();
            setEnableState(false);
        }
    }

    @Override
    public void onDisable() {
        try {
            executor.shutdown();
            this.main.disable();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
            setEnableState(false);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return main.comm().onCommand(Optional.of(sender)
                        .filter(Player.class::isInstance)
                        .map(Player.class::cast)
                        .flatMap(this::getPlayerWrapper)
                        .map(ICommandSender.class::cast)
                        .orElseGet(() -> BukkitWrapper.sender(sender)),
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
                        .orElseGet(() -> BukkitWrapper.sender(sender)),
                command.getName(),
                label,
                args);
    }

    public PluginMain getMain() {
        return main;
    }

    public void setEnableState(boolean bool) {
        this.setEnabled(bool);
    }

    public void forEachSender(Consumer<ICommandSender> consumer) {
        Bukkit.getOnlinePlayers().stream()
                .map(BukkitWrapper::player)
                .forEach(consumer);
    }

    protected Optional<? extends AbstractPlayerWrapper> getPlayerWrapper(Player player) {
        return Optional.ofNullable(player)
                .map(Entity::getUniqueId)
                .flatMap(this::getPlayerWrapper);
    }

    protected abstract Optional<? extends AbstractPlayerWrapper> getPlayerWrapper(UUID uuid);
}
