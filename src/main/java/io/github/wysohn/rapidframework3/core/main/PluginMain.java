package io.github.wysohn.rapidframework3.core.main;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.github.wysohn.rapidframework3.core.api.ManagerExternalAPI;
import io.github.wysohn.rapidframework3.core.caching.AbstractManagerElementCaching;
import io.github.wysohn.rapidframework3.core.command.ArgumentMappers;
import io.github.wysohn.rapidframework3.core.command.ManagerCommand;
import io.github.wysohn.rapidframework3.core.command.SubCommand;
import io.github.wysohn.rapidframework3.core.command.TabCompleters;
import io.github.wysohn.rapidframework3.core.database.migration.MigrationHelper;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginAsyncExecutor;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginPlatform;
import io.github.wysohn.rapidframework3.core.language.DefaultLangs;
import io.github.wysohn.rapidframework3.core.language.ManagerLanguage;
import io.github.wysohn.rapidframework3.interfaces.plugin.IDebugStateHandle;
import io.github.wysohn.rapidframework3.interfaces.plugin.IShutdownHandle;
import io.github.wysohn.rapidframework3.interfaces.plugin.ITaskSupervisor;
import io.github.wysohn.rapidframework3.interfaces.plugin.PluginRuntime;
import io.github.wysohn.rapidframework3.utils.Validation;
import io.github.wysohn.rapidframework3.utils.graph.DependencyGraph;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * (Injectable)
 * <p>
 * The class that holds all Managers and Mediators and some of the fields necessary for
 * the plugin to function.
 */
@Singleton
public class PluginMain implements PluginRuntime {
    @Inject
    private Injector injector;

    @Inject
    @PluginPlatform
    private Object platform;

    @Inject
    @PluginAsyncExecutor
    private ExecutorService executorService;

    @Inject
    private IShutdownHandle shutdownHandle;

    @Inject
    private Map<Class<? extends Manager>, Manager> managerMap;
    @Inject
    private Map<Class<? extends Mediator>, Mediator> mediatorMap;

    @Inject
    private ITaskSupervisor taskSupervisor;

    @Inject
    private IDebugStateHandle debugStateHandle;

    @Inject
    @Named("pluginName")
    private String pluginName;
    @Inject
    @Named("description")
    private String description;
    @Inject
    @Named("rootPermission")
    private String rootPermission;
    @Inject
    private Logger logger;
    @Inject
    @PluginDirectory
    private File pluginDirectory;

    private ManagerCommand comm;
    private ManagerExternalAPI api;
    private ManagerConfig conf;
    private ManagerLanguage lang;

    private final List<Manager> orderedManagers = new ArrayList<>();

    private MigrationProcess migrationProcess;

    PluginMain() {
    }

    public <T> T getPlatform() {
        return (T) platform;
    }

    public ManagerCommand comm() {
        return comm;
    }

    public ManagerExternalAPI api() {
        return api;
    }

    public ManagerConfig conf() {
        return conf;
    }

    public ManagerLanguage lang() {
        return lang;
    }

    public ITaskSupervisor task() {
        return taskSupervisor;
    }

    public boolean isDebugging() {
        return debugStateHandle.isDebugging();
    }

    public void setDebugging(boolean state) {
        debugStateHandle.setDebugging(state);
    }

    public <M extends Manager> Optional<M> getManager(Class<M> clazz) {
        return Optional.ofNullable(managerMap.get(clazz)).map(clazz::cast);
    }

    public <M extends Mediator> Optional<M> getMediator(Class<M> clazz) {
        return Optional.ofNullable(mediatorMap.get(clazz)).map(clazz::cast);
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getDescription() {
        return description;
    }

    public String getRootPermission() {
        return rootPermission;
    }

    public Logger getLogger() {
        return logger;
    }

    public File getPluginDirectory() {
        return pluginDirectory;
    }

    public List<Manager> getOrderedManagers() {
        return orderedManagers;
    }

    public Collection<Mediator> getMediators() {
        return mediatorMap.values();
    }

    @Override
    public void preload() throws Exception {
        comm = getManager(ManagerCommand.class).orElseThrow(() ->
                new RuntimeException("ManagerCommand must exist."));
        api = getManager(ManagerExternalAPI.class).orElseThrow(() ->
                new RuntimeException("ManagerExternalAPI must exist."));
        conf = getManager(ManagerConfig.class).orElseThrow(() ->
                new RuntimeException("ManagerConfig must exist."));
        lang = getManager(ManagerLanguage.class).orElseThrow(() ->
                new RuntimeException("ManagerLanguage must exist."));
        Validation.assertNotNull(taskSupervisor, "ITaskSupervisor must exist.");

        try {
            logger.info("Resolving dependency of managers...");
            orderedManagers.addAll(resolveDependencies());
        } finally {
            logger.info("Complete.");
            logger.fine("Load order: " + orderedManagers.stream()
                    .map(Object::getClass)
                    .map(Class::getSimpleName)
                    .reduce((a, b) -> a + " -> " + b)
                    .orElse("null"));
        }

        for (Manager manager : orderedManagers) {
            manager.preload();
        }

        for (Mediator mediator : mediatorMap.values()) {
            mediator.preload();
        }

        comm.addCommand(new SubCommand.Builder("reload")
                                .withDescription(DefaultLangs.Command_Reload_Description)
                                .addUsage(DefaultLangs.Command_Reload_Usage)
                                .action(((sender, args) -> {
                                    try {
                                        load();
                                        lang.sendMessage(sender, DefaultLangs.Command_Reload_Done);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                    return true;
                                })));

        comm.addCommand(new SubCommand.Builder("debug")
                                .withDescription(DefaultLangs.Command_Debug_Description)
                                .addUsage(DefaultLangs.Command_Debug_Usage)
                                .action(((sender, args) -> {
                                    if (debugStateHandle.isDebugging()) {
                                        debugStateHandle.setDebugging(false);
                                        lang.sendMessage(sender, DefaultLangs.Command_Debug_State, (sen, man) ->
                                                man.addString("&7false"));

                                    } else {
                                        debugStateHandle.setDebugging(true);
                                        lang.sendMessage(sender, DefaultLangs.Command_Debug_State, (sen, man) ->
                                                man.addString("&atrue"));
                                    }
                                    return true;
                                })));

        comm.addCommand(new SubCommand.Builder("migrate", 0)
                                .withDescription(DefaultLangs.Command_Migrate_Description)
                                .addUsage(DefaultLangs.Command_Migrate_Usage)
                                .addArgumentMapper(0, ArgumentMappers.STRING)
                                .addTabCompleter(0, TabCompleters.simple("mysql", "file", "h2"))
                                .action((sender, args) -> {
                                    String from = args.get(0).map(String.class::cast).orElse(null);
                                    if (from == null) {
                                        sender.sendMessageRaw("Invalid dbType");
                                        return true;
                                    }

                                    String to = conf.get("dbType")
                                            .map(String.class::cast)
                                            .orElse("h2");

                                    if (from.equals(to)) {
                                        sender.sendMessageRaw("You are already using " + from + " data source");
                                        return true;
                                    }

                                    if(migrationProcess != null && migrationProcess.getState() != Thread.State.TERMINATED){
                                        sender.sendMessageRaw("Already under progress.");
                                        return true;
                                    }

                                    sender.sendMessageRaw("Migration scheduled (" + from + " -> " + to + ")");
                                    migrationProcess = new MigrationProcess(logger,
                                                                            managerMap.values().stream()
                                                                                    .filter(AbstractManagerElementCaching.class::isInstance)
                                                                                    .map(AbstractManagerElementCaching.class::cast)
                                                                                    .collect(Collectors.toList()),
                                                                            from);
                                    migrationProcess.start();

                                    return true;
                                }));
    }

    private Collection<Manager> resolveDependencies() {
        List<Class<? extends Manager>> unordered = new ArrayList<>(managerMap.keySet());
        List<Manager> unorderedInst = unordered.stream()
                .map(managerMap::get)
                .collect(Collectors.toList());
        Map<Manager, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < unorderedInst.size(); i++)
            indexMap.put(unorderedInst.get(i), i);

        DependencyGraph graph = new DependencyGraph(unorderedInst);
        for (int i = 0; i < unorderedInst.size(); i++) {
            int nodeIndex = i;
            Manager current = unorderedInst.get(i);
            current.dependsOn.stream()
                    .map(clazz -> {
                        if (!managerMap.containsKey(clazz))
                            throw new RuntimeException(Optional.of(current)
                                    .map(Object::getClass)
                                    .map(Class::getSimpleName)
                                    .orElse(null) + " depends on " + clazz.getSimpleName() + ", but it's not registered!");

                        return managerMap.get(clazz);
                    })
                    .map(indexMap::get)
                    .forEach(edgeIndex -> graph.addEdge(nodeIndex, edgeIndex));
        }
        return graph.resolveDependency();
    }

    @Override
    public void enable() throws Exception {
        for (Manager manager : orderedManagers) {
            manager.enable();
        }

        for (Mediator mediator : mediatorMap.values()) {
            mediator.enable();
        }
    }

    @Override
    public void load() throws Exception {
        for (Manager manager : orderedManagers) {
            manager.load();
        }

        for (Mediator mediator : mediatorMap.values()) {
            mediator.load();
        }
    }

    @Override
    public void disable() throws Exception {
        for (Mediator mediator : mediatorMap.values()) {
            mediator.disable();
        }

        for (int i = orderedManagers.size() - 1; i >= 0; i--) {
            orderedManagers.get(i).disable();
        }

        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);
    }

    public void shutdown() {
        shutdownHandle.shutdown();
    }

    private static class MigrationProcess extends Thread{
        private final Logger logger;
        private final List<AbstractManagerElementCaching<?, ?>> managers;
        private final String from;

        public MigrationProcess(Logger logger,
                                List<AbstractManagerElementCaching<?, ?>> managers,
                                String from) {
            this.logger = logger;
            this.managers = managers;
            this.from = from;
        }

        @Override
        public void run() {
            managers.forEach(manager -> {
                MigrationHelper<?, ?, ?> helper = manager.migrateFrom(from);

                logger.info("Migration started for "+manager);
                helper.start();
                helper.waitForTermination(1, TimeUnit.DAYS);
                logger.info("Migration is done for "+manager);
            });
            logger.info("All managers are migrated");
        }
    }
}
