package io.github.wysohn.rapidframework2.core.main;

import io.github.wysohn.rapidframework2.core.interfaces.entity.IPluginManager;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.PluginRuntime;
import io.github.wysohn.rapidframework2.core.manager.api.ManagerExternalAPI;
import io.github.wysohn.rapidframework2.core.manager.command.ManagerCommand;
import io.github.wysohn.rapidframework2.core.manager.common.AbstractFileSession;
import io.github.wysohn.rapidframework2.core.manager.config.ManagerConfig;
import io.github.wysohn.rapidframework2.core.manager.lang.DefaultLangs;
import io.github.wysohn.rapidframework2.core.manager.lang.Lang;
import io.github.wysohn.rapidframework2.core.manager.lang.LanguageSessionFactory;
import io.github.wysohn.rapidframework2.core.manager.lang.ManagerLanguage;
import util.Validation;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class PluginMain implements PluginRuntime {
    private final Map<Class<? extends Manager>, Manager> managers = new HashMap<>();
    private final Map<String, Manager> managersStr = new HashMap<>();
    private final List<Manager> orderedManagers = new ArrayList<>();

    private final String pluginName;
    private final String description;
    private final ManagerCommand comm;
    private final String adminPermission;
    private final PluginBridge pluginBridge;
    private final Logger logger;
    private final File pluginDirectory;

    private ManagerExternalAPI api;
    private ManagerConfig conf;
    private ManagerLanguage lang;

    private PluginMain(String pluginName, String description, String mainCommand, String adminPermission,
                       PluginBridge pluginBridge, Logger logger, File pluginDirectory) {
        this.pluginName = pluginName;
        this.description = description;
        this.comm = new ManagerCommand(Manager.FASTEST_PRIORITY, mainCommand);
        this.adminPermission = adminPermission;
        this.pluginBridge = pluginBridge;
        this.logger = logger;
        this.pluginDirectory = pluginDirectory;
    }

    private static List<Manager>[] getManagersByPriority(Map<Class<? extends Manager>, Manager> managers) {
        List<Manager>[] prioritized = new List[Manager.SLOWEST_PRIORITY - Manager.FASTEST_PRIORITY + 1];
        Map<Integer, List<Manager>> map = new TreeMap<>();

        for (int i = Manager.FASTEST_PRIORITY; i <= Manager.SLOWEST_PRIORITY; i++) {
            prioritized[i] = new ArrayList<>();
        }

        managers.forEach((clazz, manager) -> {
            prioritized[manager.getLoadPriority() - Manager.FASTEST_PRIORITY].add(manager);
        });

        return prioritized;
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getDescription() {
        return description;
    }

    public ManagerCommand comm() {
        return comm;
    }

    public ManagerConfig conf() {
        return conf;
    }

    public ManagerExternalAPI api() {
        return api;
    }

    public ManagerLanguage lang() {
        return lang;
    }

    public String getAdminPermission() {
        return adminPermission;
    }

    public PluginBridge getPluginBridge() {
        return pluginBridge;
    }

    private void registerManager(Manager manager) {
        managers.put(manager.getClass(), manager);
        managersStr.put(manager.getClass().getSimpleName(), manager);

        manager.main = this;
    }

    public <T extends Manager> T getManager(Class<T> clazz){
        return (T) managers.get(clazz);
    }

    public Manager getManager(String managerName){
        return managersStr.get(managerName);
    }

    public Collection<Manager> getOrderedManagers() {
        return Collections.unmodifiableCollection(orderedManagers);
    }

    @Override
    public void enable() throws Exception {
        registerManager(conf);
        registerManager(comm);
        registerManager(lang);
        registerManager(api);

        Arrays.stream(getManagersByPriority(managers))
                .flatMap(List::stream)
                .forEachOrdered(orderedManagers::add);

        for (Manager manager : orderedManagers) {
            try{
                manager.enable();
            } catch (Exception ex){

            }
        }
    }

    @Override
    public void load() throws Exception {
        for (Manager manager : orderedManagers) {
            manager.load();
        }
    }

    @Override
    public void disable() throws Exception {
        for (Manager manager : orderedManagers) {
            manager.disable();
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public File getPluginDirectory() {
        return pluginDirectory;
    }

    public static class Builder {
        private PluginMain main;

        private Builder() {

        }

        public static Builder prepare(String pluginName,
                                      String pluginDesc,
                                      String mainCommand,
                                      String adminPermission,
                                      PluginBridge pluginBridge,
                                      Logger logger,
                                      File pluginDirectory) {
            Builder builder = new Builder();
            builder.main = new PluginMain(pluginName, pluginDesc, mainCommand, adminPermission,
                    pluginBridge, logger, pluginDirectory);
            return builder;
        }

        public Builder andConfigSession(AbstractFileSession session) {
            main.conf = new ManagerConfig(Manager.FASTEST_PRIORITY, session);
            main.registerManager(main.conf);
            return this;
        }

        public Builder andPluginSupervisor(IPluginManager pluginManager) {
            main.api = new ManagerExternalAPI(Manager.FASTEST_PRIORITY, pluginManager);
            return this;
        }

        public Builder andLanguageSessionFactory(LanguageSessionFactory factory) {
            main.lang = new ManagerLanguage(Manager.FASTEST_PRIORITY, factory);

            return this;
        }

        public <T extends Lang> Builder addLangs(T[] langs) {
            Validation.assertNotNull(main.lang, "Register ManagerLanguage with .andLanguageSessionFactory() first.");

            Stream.of(langs)
                    .filter(Objects::nonNull)
                    .forEach(main.lang::registerLanguage);
            return this;
        }

        public Builder withManagers(Manager... managers) {
            Stream.of(managers).forEach(main::registerManager);
            return this;
        }

        public PluginMain build() {
            Validation.assertNotNull(main.conf, "Register config with .andFileSession() first.");
            Validation.assertNotNull(main.api, "Register IPluginManager with .andPluginManager() first.");
            Validation.assertNotNull(main.lang, "Register ManagerLanguage with .andLanguageSessionFactory() first.");

            addLangs(DefaultLangs.values());

            return main;
        }
    }

    public static abstract class Manager implements PluginRuntime {
        private final int loadPriority;

        private PluginMain main;

        public Manager(int loadPriority) {
            this.loadPriority = loadPriority;
        }

        public PluginMain main() {
            return main;
        }

        public int getLoadPriority() {
            return loadPriority;
        }
        
        public static final int NORM_PRIORITY = 5;
        public static final int SLOWEST_PRIORITY = 10;
        public static final int FASTEST_PRIORITY = 0;
    }
}
