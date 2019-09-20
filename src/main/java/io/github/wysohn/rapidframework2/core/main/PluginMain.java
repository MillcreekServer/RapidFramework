package io.github.wysohn.rapidframework2.core.main;

import io.github.wysohn.rapidframework2.core.interfaces.entity.IPluginManager;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.PluginRuntime;
import io.github.wysohn.rapidframework2.core.manager.api.ManagerExternalAPI;
import io.github.wysohn.rapidframework2.core.manager.command.ManagerCommand;
import io.github.wysohn.rapidframework2.core.manager.common.AbstractFileSession;
import io.github.wysohn.rapidframework2.core.manager.common.Manager;
import io.github.wysohn.rapidframework2.core.manager.config.ManagerConfig;
import io.github.wysohn.rapidframework2.core.manager.lang.Lang;
import io.github.wysohn.rapidframework2.core.manager.lang.ManagerLanguage;
import util.Validation;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class PluginMain implements PluginRuntime {
    private final Map<Class<? extends Manager>, Manager> managers = new HashMap<>();
    private final Map<String, Manager> managersStr = new HashMap<>();
    private final List<Manager> orderedManagers = new ArrayList<>();

    private final ManagerCommand comm;
    private final ManagerLanguage lang;
    private final String adminPermission;
    private final Logger logger;
    private ManagerExternalAPI api;
    private ManagerConfig conf;

    private PluginMain(String mainCommand, String adminPermission, Logger logger) {
        this.comm = new ManagerCommand(this, Manager.FASTEST_PRIORITY, mainCommand);
        this.lang = new ManagerLanguage(this, Manager.FASTEST_PRIORITY);

        registerManager(comm);
        registerManager(lang);
        registerManager(api);

        this.adminPermission = adminPermission;
        this.logger = logger;
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

    public Logger getLogger() {
        return logger;
    }

    public void registerManager(Manager manager) {
        managers.put(manager.getClass(), manager);
        managersStr.put(manager.getClass().getSimpleName(), manager);
    }

    @Override
    public void enable() throws Exception {
        Arrays.stream(getManagersByPriority(managers))
                .flatMap(List::stream)
                .forEachOrdered(orderedManagers::add);

        for (Manager manager : orderedManagers) {
            manager.enable();
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

    public static class Builder {
        private PluginMain main;

        private Builder() {

        }

        public static Builder beginWith(String mainCommand, String adminPermission, Logger logger) {
            Builder builder = new Builder();
            builder.main = new PluginMain(mainCommand, adminPermission, logger);
            return builder;
        }

        public Builder andConfigSession(AbstractFileSession session) {
            main.conf = new ManagerConfig(main, Manager.FASTEST_PRIORITY, session);
            main.registerManager(main.conf);
            return this;
        }

        public Builder andPluginManager(IPluginManager pluginManager) {
            main.api = new ManagerExternalAPI(main, Manager.FASTEST_PRIORITY, pluginManager);
            return this;
        }

        public <T extends Enum<? extends Lang>> Builder withLangs(T... langs) {
            Stream.of(langs)
                    .filter(Objects::nonNull)
                    .forEach(main.lang::registerLanguage);
            return this;
        }

        public Builder withManagers(Manager... managers) {
            Stream.of(managers)
                    .forEach(main::registerManager);
            return this;
        }

        public PluginMain build() {
            Validation.assertNotNull(main.conf, "Register config with andFileSession() first.");

            return main;
        }
    }
}
