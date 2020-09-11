package io.github.wysohn.rapidframework3.core.main;

import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;
import io.github.wysohn.rapidframework3.core.interfaces.plugn.ITaskSupervisor;
import io.github.wysohn.rapidframework3.core.interfaces.plugn.PluginRuntime;
import io.github.wysohn.rapidframework3.core.language.ManagerLanguage;
import io.github.wysohn.rapidframework3.utils.graph.DependencyGraph;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
public class PluginMain implements PluginRuntime {
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

    //    private ManagerCommand comm;
//    private ManagerExternalAPI api;
    private ManagerConfig conf;
    private ManagerLanguage lang;

    private final List<Manager> orderedManagers = new ArrayList<>();

    public <M extends Manager> M getManager(Class<M> clazz) {
        return (M) managerMap.get(clazz);
    }

    public <M extends Mediator> M getMediator(Class<M> clazz) {
        return (M) mediatorMap.get(clazz);
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

    @Override
    public void preload() throws Exception {
        conf = Objects.requireNonNull(getManager(ManagerConfig.class));
        lang = Objects.requireNonNull(getManager(ManagerLanguage.class));

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
}