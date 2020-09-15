package io.github.wysohn.rapidframework3.core.main;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.github.wysohn.rapidframework3.core.api.ManagerExternalAPI;
import io.github.wysohn.rapidframework3.core.command.ManagerCommand;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginPlatform;
import io.github.wysohn.rapidframework3.core.language.ManagerLanguage;
import io.github.wysohn.rapidframework3.interfaces.plugin.ITaskSupervisor;
import io.github.wysohn.rapidframework3.interfaces.plugin.PluginRuntime;
import io.github.wysohn.rapidframework3.utils.graph.DependencyGraph;

import java.io.File;
import java.util.*;
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
    @PluginPlatform
    private Object platform;

    @Inject
    private Map<Class<? extends Manager>, Manager> managerMap;
    @Inject
    private Map<Class<? extends Mediator>, Mediator> mediatorMap;

    @Inject
    private ITaskSupervisor taskSupervisor;

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
            unorderedInst.get(i).dependsOn.stream()
                    .map(managerMap::get)
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

        for (Manager manager : orderedManagers) {
            manager.disable();
        }
    }

    public void shutdown() {
        //TODO
    }
}
