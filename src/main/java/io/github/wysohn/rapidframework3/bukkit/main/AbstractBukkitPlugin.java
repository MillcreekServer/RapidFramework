package io.github.wysohn.rapidframework3.bukkit.main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.bukkit.data.BukkitWrapper;
import io.github.wysohn.rapidframework3.bukkit.inject.module.*;
import io.github.wysohn.rapidframework3.bukkit.manager.api.PlaceholderAPI;
import io.github.wysohn.rapidframework3.bukkit.manager.api.ProtocolLibAPI;
import io.github.wysohn.rapidframework3.bukkit.manager.message.QueuedMessageManager;
import io.github.wysohn.rapidframework3.core.command.SubCommand;
import io.github.wysohn.rapidframework3.core.inject.module.*;
import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.core.main.PluginMainBuilder;
import io.github.wysohn.rapidframework3.core.player.AbstractPlayerWrapper;
import io.github.wysohn.rapidframework3.interfaces.ICommandSender;
import io.github.wysohn.rapidframework3.interfaces.io.IPluginResourceProvider;
import io.github.wysohn.rapidframework3.interfaces.message.IQueuedMessageConsumer;
import io.github.wysohn.rapidframework3.interfaces.plugin.IShutdownHandle;
import io.github.wysohn.rapidframework3.utils.JarUtil;
import io.github.wysohn.rapidframework3.utils.Pair;
import io.github.wysohn.rapidframework3.utils.Validation;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public abstract class AbstractBukkitPlugin extends JavaPlugin {
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

    /**
     * @param className CraftBukkit class name with/without packages. For example, CraftItemStack can be
     *                  retrieved with "inventory.CraftItemStack"
     * @return CraftBukkit class corresspond to the currnet server's version
     */
    public static Class<?> getCraftBukkitClass(String className) throws ClassNotFoundException {
        Validation.assertNotNull(className);
        return Class.forName("org.bukkit.craftbukkit." + nmsVersion + "." + className);
    }

    private final Server server;

    private boolean test;
    private PluginMain main;

    public AbstractBukkitPlugin() {
        server = Bukkit.getServer();
    }

    /**
     * @param mockServer
     * @deprecated for test only
     */
    protected AbstractBukkitPlugin(@NotNull Server mockServer) {
        super(new JavaPluginLoader(mockServer),
                new PluginDescriptionFile("test", "test", "test"),
                new File("build/tmp/tests/"),
                new File("build/tmp/tests/other"));
        server = mockServer;
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

        getLogger().addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (record.getLevel() == Level.FINE) {
                    String loggerName = record.getLoggerName();
                    System.out.println(record.getMessage());
                }
            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        });

        PluginMainBuilder builder = PluginMainBuilder.prepare(new BukkitPluginInfoModule(getDescription()),
                test ? new MainCommandsModule("test") : new BukkitMainCommandsModule(getDescription()),
                new LoggerModule(getLogger()),
                new PluginDirectoryModule(getDataFolder()));
        builder.addModule(new PlatformModule(this));
        builder.addModule(new DefaultManagersModule());
        builder.addModule(new ManagerModule(
                QueuedMessageManager.class
        ));
        builder.addModule(new MediatorModule());
        builder.addModule(new FileIOModule());
        builder.addModule(new BukkitStorageFactoryModule());
        builder.addModule(new BukkitBroadcasterModule());
        builder.addModule(test ? new GlobalPluginManagerModule(pluginName -> false) : new BukkitPluginManagerModule());
        builder.addModule(new BukkitTaskSupervisorModule());
        builder.addModule(new ExternalAPIModule(
                Pair.of("ProtocolLib", ProtocolLibAPI.class),
                Pair.of("PlaceholderAPI", PlaceholderAPI.class)
        ));
        builder.addModule(new BukkitMessageSenderModule());
        builder.addModule(new AbstractModule() {
            @Provides
            @Singleton
            IShutdownHandle shutdownModule() {
                return () -> setEnabled(false);
            }

            @Provides
            @Singleton
            IPluginResourceProvider resourceProvider() {
                return filename -> getResource(filename);
            }

            @Provides
            @Singleton
            IPlayerWrapper playerWrapper() {
                return uuid -> getPlayerWrapper(uuid).orElse(null);
            }

            @Provides
            @Singleton
            IQueuedMessageConsumer queuedMessageConsumer(QueuedMessageManager manager) {
                return manager;
            }
        });
        init(builder);
        this.main = builder.build();

        try {
            this.main.preload();
        } catch (Exception ex) {
            ex.printStackTrace();
            setEnabled(false);
        }
    }

    protected abstract void init(PluginMainBuilder builder);

    protected abstract void registerCommands(List<SubCommand.Builder> commands);

    @Override
    public void onEnable() {
        try {
            this.main.enable();
            this.main.load();

            List<SubCommand.Builder> commands = new ArrayList<>();
            registerCommands(commands);
            for (SubCommand.Builder command : commands) {
                main.comm().addCommand(command);
            }
            commands.forEach(main.comm()::addCommand);

            getDescription().getCommands().keySet().stream()
                    .map(this::getCommand)
                    .filter(Objects::nonNull)
                    .forEach(pluginCommand -> pluginCommand.setTabCompleter(this));

            main.getOrderedManagers().stream()
                    .filter(Listener.class::isInstance)
                    .map(Listener.class::cast)
                    .forEach(manager -> Optional.ofNullable(server.getPluginManager())
                            .ifPresent(pluginManager -> pluginManager.registerEvents(manager, this)));

            main.getMediators().stream()
                    .filter(Listener.class::isInstance)
                    .map(Listener.class::cast)
                    .forEach(mediator -> Optional.ofNullable(server.getPluginManager())
                            .ifPresent(pluginManager -> pluginManager.registerEvents(mediator, this)));
        } catch (Exception ex) {
            ex.printStackTrace();
            setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        try {
            this.main.disable();
        } catch (Exception ex) {
            ex.printStackTrace();
            setEnabled(false);
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

    public void forEachSender(Consumer<ICommandSender> consumer) {
        server.getOnlinePlayers().stream()
                .map(BukkitWrapper::player)
                .forEach(consumer);
    }

    protected Optional<? extends AbstractPlayerWrapper> getPlayerWrapper(Player player) {
        return Optional.ofNullable(player)
                .map(Entity::getUniqueId)
                .flatMap(this::getPlayerWrapper);
    }

    protected abstract Optional<? extends AbstractPlayerWrapper> getPlayerWrapper(UUID uuid);

    public interface IPlayerWrapper {
        AbstractPlayerWrapper wrap(UUID uuid);
    }
}
