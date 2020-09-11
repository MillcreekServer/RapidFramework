package io.github.wysohn.rapidframework3.core.api;

import io.github.wysohn.rapidframework3.core.interfaces.plugin.IGlobalPluginManager;
import io.github.wysohn.rapidframework3.core.main.Manager;
import io.github.wysohn.rapidframework3.core.main.PluginMain;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * This class allow plugins to be loaded while using the APIs of third party plugins.
 * NoClassDefFoundError is thrown where the plugin expect the class to be existing, yet it might not be the case
 * (since the server can load without the plugin, which contains the required class). Therefore, the best way to deal
 * with it is to dynamically instantiate the class only if the target third party plugin actually exist and enabled.
 */
public class ManagerExternalAPI extends Manager {
    private final Map<String, Set<Class<? extends ExternalAPI>>> apiClasses = new HashMap<>();
    private final Map<Class<? extends ExternalAPI>, ExternalAPI> externalAPIs = new HashMap<>();

    private final IGlobalPluginManager pluginManager;

    @Inject
    public ManagerExternalAPI(PluginMain main,
                              IGlobalPluginManager pluginManager,
                              Map<String, Class<? extends ExternalAPI>> apis) {
        super(main);
        this.pluginManager = pluginManager;
        apis.forEach((name, clazz) -> apiClasses.computeIfAbsent(name, (key) -> new HashSet<>()).add(clazz));
    }

    @Override
    public void enable() throws Exception {
        for (Map.Entry<String, Set<Class<? extends ExternalAPI>>> entry : apiClasses.entrySet()) {
            String pluginName = entry.getKey();
            Set<Class<? extends ExternalAPI>> clazzes = entry.getValue();

            if (!pluginManager.isEnabled(pluginName))
                continue;

            clazzes.forEach(clazz -> {
                try {
                    Constructor con = clazz.getConstructor(PluginMain.class, String.class);
                    ExternalAPI api = (ExternalAPI) con.newInstance(main(), pluginName);
                    api.enable();

                    externalAPIs.put(clazz, api);

                    main().getLogger().info("[" + clazz.getSimpleName() + "] Hooked to " + pluginName + ".");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    main().getLogger().severe("Failed to enable API support for [" + pluginName + "]");
                }
            });
        }
    }

    @Override
    public void load() throws Exception {
        for (Map.Entry<Class<? extends ExternalAPI>, ExternalAPI> entry : externalAPIs.entrySet()) {
            Class<? extends ExternalAPI> clazz = entry.getKey();
            ExternalAPI api = entry.getValue();

            try {
                api.load();

                main().getLogger().info("[" + clazz.getSimpleName() + "] load complete.");
            } catch (Exception ex) {
                ex.printStackTrace();
                main().getLogger().severe("Failed to load API support for [" + clazz.getSimpleName() + "]");
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

                main().getLogger().info("[" + clazz.getSimpleName() + "] disabled.");
            } catch (Exception ex) {
                ex.printStackTrace();
                main().getLogger().severe("Failed to load API support for [" + clazz.getSimpleName() + "]");
            }
        }
    }

    public <T extends ExternalAPI> Optional<T> getAPI(Class<T> clazz) {
        return Optional.ofNullable((T) externalAPIs.get(clazz));
    }
}