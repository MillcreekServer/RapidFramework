package io.github.wysohn.rapidframework4.core.api;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework4.core.inject.annotations.PluginLogger;
import io.github.wysohn.rapidframework4.core.main.Manager;
import io.github.wysohn.rapidframework4.core.main.PluginMain;
import io.github.wysohn.rapidframework4.interfaces.plugin.IGlobalPluginManager;
import io.github.wysohn.rapidframework4.utils.Pair;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.logging.Logger;

/**
 * This class allow plugins to be loaded while using the APIs of third party plugins.
 * NoClassDefFoundError is thrown where the plugin expect the class to be existing, yet it might not be the case
 * (since the server can load without the plugin, which contains the required class). Therefore, the best way to deal
 * with it is to dynamically instantiate the class only if the target third party plugin actually exist and enabled.
 */
@Singleton
public class ManagerExternalAPI extends Manager {
    private final Map<String, Set<Class<? extends ExternalAPI>>> apiClasses = new HashMap<>();
    private final Map<Class<? extends ExternalAPI>, ExternalAPI> externalAPIs = new HashMap<>();

    private final IGlobalPluginManager pluginManager;
    private final Logger logger;
    private final PluginMain main;
    private final Injector injector;

    @Inject
    public ManagerExternalAPI(PluginMain main,
                              @PluginLogger Logger logger,
                              IGlobalPluginManager pluginManager,
                              Injector injector,
                              Set<Pair<String, Class<? extends ExternalAPI>>> apis) {
        super();
        this.main = main;
        this.logger = logger;
        this.pluginManager = pluginManager;
        this.injector = injector;
        apis.forEach(pair -> apiClasses.computeIfAbsent(pair.key, (key) -> new HashSet<>()).add(pair.value));
    }

    @Override
    public void enable() throws Exception {
        for (Map.Entry<String, Set<Class<? extends ExternalAPI>>> entry : apiClasses.entrySet()) {
            String pluginName = entry.getKey();
            Set<Class<? extends ExternalAPI>> clazzes = entry.getValue();

            if (!pluginManager.isEnabled(pluginName))
                continue;

            for (Class<? extends ExternalAPI> clazz : clazzes) {
                try {
                    Constructor con = clazz.getConstructor(PluginMain.class, String.class);
                    ExternalAPI api = (ExternalAPI) con.newInstance(main, pluginName);
                    injector.injectMembers(api);

                    api.enable();

                    externalAPIs.put(clazz, api);

                    logger.info("[" + clazz.getSimpleName() + "] Hooked to " + pluginName + ".");
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    logger.severe("Failed to enable API support for [" + pluginName + "]");
                }
            }
        }
    }

    @Override
    public void load() throws Exception {
        for (Map.Entry<Class<? extends ExternalAPI>, ExternalAPI> entry : externalAPIs.entrySet()) {
            Class<? extends ExternalAPI> clazz = entry.getKey();
            ExternalAPI api = entry.getValue();

            try {
                api.load();

                logger.info("[" + clazz.getSimpleName() + "] load complete.");
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.severe("Failed to load API support for [" + clazz.getSimpleName() + "]");
            }
        }
    }

    @Override
    public void disable() throws Exception {
        for (Map.Entry<Class<? extends ExternalAPI>, ExternalAPI> entry : externalAPIs.entrySet()) {
            Class<? extends ExternalAPI> clazz = entry.getKey();
            ExternalAPI api = entry.getValue();

            try {
                api.disable();

                logger.info("[" + clazz.getSimpleName() + "] disabled.");
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.severe("Failed to load API support for [" + clazz.getSimpleName() + "]");
            }
        }
    }

    public <T extends ExternalAPI> Optional<T> getAPI(Class<T> clazz) {
        return Optional.ofNullable((T) externalAPIs.get(clazz));
    }
}