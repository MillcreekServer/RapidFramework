package io.github.wysohn.rapidframework2.bukkit.main;

import io.github.wysohn.rapidframework2.bukkit.main.config.ConfigFileSession;
import io.github.wysohn.rapidframework2.bukkit.main.objects.BukkitPlayer;
import io.github.wysohn.rapidframework2.bukkit.manager.api.PlaceholderAPI;
import io.github.wysohn.rapidframework2.bukkit.manager.api.ProtocolLibAPI;
import io.github.wysohn.rapidframework2.core.interfaces.ITaskSupervisor;
import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.IPluginManager;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.chat.IPlaceholderSupport;
import io.github.wysohn.rapidframework2.core.manager.command.SubCommand;
import io.github.wysohn.rapidframework2.core.manager.common.message.IMessageSender;
import io.github.wysohn.rapidframework2.core.manager.common.message.Message;
import io.github.wysohn.rapidframework2.core.manager.lang.LanguageSession;
import io.github.wysohn.rapidframework2.tools.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.logging.Logger;

public abstract class BukkitPluginBridge implements io.github.wysohn.rapidframework2.core.main.PluginBridge {
    private final AbstractBukkitPlugin bukkit;
    private final PluginMain.Builder mainBuilder;

    private PluginMain main;

    public BukkitPluginBridge(AbstractBukkitPlugin bukkit) {
        this(bukkit.getDescription().getName(),
                Optional.of(bukkit)
                        .map(JavaPlugin::getDescription)
                        .map(PluginDescriptionFile::getDescription)
                        .orElse("No plugin description."),
                bukkit.getDescription().getCommands()
                        .entrySet()
                        .stream()
                        .findFirst()
                        .map(Map.Entry::getKey)
                        .orElse(null),
                bukkit.getDescription().getPermissions()
                        .stream()
                        .findFirst()
                        .map(Permission::getName)
                        .orElse(bukkit.getDescription().getName().toLowerCase()),
                bukkit.getLogger(),
                bukkit.getDataFolder(),
                pluginName -> bukkit.getPluginManager().isPluginEnabled(pluginName),
                bukkit);
    }

    public BukkitPluginBridge(String pluginName,
                              String pluginDescription,
                              String mainCommand,
                              String adminPermission,
                              Logger logger,
                              File dataFolder,
                              IPluginManager iPluginManager,
                              AbstractBukkitPlugin bukkit) {
        this.bukkit = bukkit;
        this.mainBuilder = PluginMain.Builder.prepare(
                pluginName,
                pluginDescription,
                mainCommand,
                adminPermission,
                this,
                logger,
                dataFolder)
                .andConfigSession(new ConfigFileSession(FileUtil.join(dataFolder, "config.yml")))
                .andPluginSupervisor(iPluginManager)
                .andLanguageSessionFactory(locale -> new LanguageSession(new ConfigFileSession(FileUtil.join(dataFolder,
                        "lang", locale.getLanguage() + ".yml"))))
                .andChatManager(new ConfigFileSession(FileUtil.join(dataFolder, "chat.yml")), new IPlaceholderSupport() {
                    @Override
                    public String parse(ICommandSender sender, String str) {
                        String formatted = String.format(str, sender.getDisplayName());

                        return main.api().getAPI(PlaceholderAPI.class)
                                .map(api -> api.parse(sender, formatted))
                                .orElse(formatted);
                    }
                })
                .andTaskSupervisor(new ITaskSupervisor() {
                    final ExecutorService executor = Executors.newCachedThreadPool(runnable -> {
                        Thread thread = new Thread(runnable);
                        thread.setName("AsyncTask - " + BukkitPluginBridge.this.getClass().getSimpleName());
                        thread.setPriority(Thread.NORM_PRIORITY - 1);
                        return thread;
                    });

                    @Override
                    public <V> Future<V> sync(Callable<V> callable) {
                        return Bukkit.getScheduler().callSyncMethod(bukkit, callable);
                    }

                    @Override
                    public void sync(Runnable runnable) {
                        Bukkit.getScheduler().runTask(bukkit, runnable);
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
                })
                .withExternalAPIs("ProtocolLib", ProtocolLibAPI.class)
                .withExternalAPIs("PlaceholderAPI", PlaceholderAPI.class)
                .setMessageSender(new IMessageSender() {
                    private boolean failure = false;

                    @Override
                    public boolean isJsonEnabled() {
                        return main.api().getAPI(ProtocolLibAPI.class).isPresent();
                    }

                    @Override
                    public void send(ICommandSender sender, Message[] message) {
                        Optional<ProtocolLibAPI> optApi = main.api().getAPI(ProtocolLibAPI.class);
                        if (failure || !optApi.isPresent()) {
                            IMessageSender.super.send(sender, message);
                            return;
                        }

                        boolean sent = false;
                        try{
                            if(sender instanceof BukkitPlayer){
                                optApi.get().send((BukkitPlayer) sender, message);
                                sent = true;
                            }
                        } catch (Exception ex){
                            ex.printStackTrace();
                            failure = true;
                        } finally {
                            // fallback just in case something went wrong so
                            // the chat continues with auxiliary sender
                            if (!sent) {
                                IMessageSender.super.send(sender, message);
                            }
                        }
                    }
                });
    }

    @Override
    public <T> T getPlatform() {
        return (T) bukkit;
    }

    @Override
    public void init() {
        try {
            main = init(mainBuilder);

            main.preload();
        } catch (Exception e) {
            e.printStackTrace();
            bukkit.setEnableState(false);
        }
    }

    @Override
    public PluginMain getMain() {
        return main;
    }

    public AbstractBukkitPlugin getBukkit() {
        return bukkit;
    }

    protected abstract PluginMain init(PluginMain.Builder builder);

    protected abstract void registerCommands(List<SubCommand> commands);

    @Override
    public void enable() {
        try {
            main.enable();

            List<SubCommand> commands = new ArrayList<>();
            registerCommands(commands);
            commands.forEach(main.comm()::addCommand);

            Optional.ofNullable(bukkit.getCommand(main.comm().getMainCommand()))
                    .ifPresent(pluginCommand -> pluginCommand.setTabCompleter(bukkit));

            main.getOrderedManagers().stream()
                    .filter(Listener.class::isInstance)
                    .map(Listener.class::cast)
                    .forEach(manager -> Optional.ofNullable(bukkit.getPluginManager())
                            .ifPresent(pluginManager -> pluginManager.registerEvents(manager, bukkit)));

            main.load();
        } catch (Exception e) {
            e.printStackTrace();
            bukkit.setEnableState(false);
        }
    }

    @Override
    public void disable() {
        try {
            main.disable();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(ICommandSender sender, String command, String label, String[] args_in) {
        return main.comm().onCommand(sender, command, label, args_in);
    }

    @Override
    public List<String> onTabComplete(ICommandSender sender, String command, String alias, String[] args) {
        return main.comm().onTabComplete(sender, command, alias, args);
    }

    @Override
    public void forEachSender(Consumer<ICommandSender> consumer) {
        bukkit.forEachSender(consumer);
    }

    @Override
    public void shutdown() {
        bukkit.setEnableState(false);
    }
}
