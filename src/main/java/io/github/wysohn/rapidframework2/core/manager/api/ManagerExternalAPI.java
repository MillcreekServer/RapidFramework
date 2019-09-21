package io.github.wysohn.rapidframework2.core.manager.api;

import io.github.wysohn.rapidframework2.core.interfaces.entity.IPluginManager;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.PluginRuntime;
import io.github.wysohn.rapidframework2.core.main.PluginMain;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * This class allow plugins to be loaded while using the APIs of third party plugins.
 * NoClassDefFoundError is thrown where the plugin expect the class to be existing, yet it might not be the case
 * (since the server can load without the plugin which has the class). Therefore, the best way to deal with it is
 * dynamically instantiate the dependent class only if the target third party plugin actually exist and enabled.
 */
public class ManagerExternalAPI extends PluginMain.Manager {
    private final Map<String, Class<? extends ExternalAPI>> apiClasses = new HashMap<>();
    private final Map<String, ExternalAPI> externalAPIs = new HashMap<>();

    private final IPluginManager pluginManager;

    public ManagerExternalAPI(int loadPriority, IPluginManager pluginManager) {
        super(loadPriority);

        this.pluginManager = pluginManager;
    }

    @Override
    public void enable() throws Exception {
        for (Map.Entry<String, Class<? extends ExternalAPI>> entry : apiClasses.entrySet()) {
            String pluginName = entry.getKey();
            Class<? extends ExternalAPI> clazz = entry.getValue();

            if (!pluginManager.isEnabled(pluginName))
                continue;

            try {
                Constructor con = clazz.getConstructor(PluginMain.class, String.class);
                ExternalAPI api = (ExternalAPI) con.newInstance(main(), pluginName);
                api.enable();

                externalAPIs.put(pluginName, api);

            } catch (Exception ex) {
                ex.printStackTrace();
                main().getLogger().severe("Failed to enable API support for [" + pluginName + "]");
            }
        }
    }

    @Override
    public void load() throws Exception {
        for (Map.Entry<String, ExternalAPI> entry : externalAPIs.entrySet()) {
            String pluginName = entry.getKey();
            ExternalAPI api = entry.getValue();

            try {
                api.load();
            } catch (Exception ex) {
                ex.printStackTrace();
                main().getLogger().severe("Failed to load API support for [" + pluginName + "]");
            }
        }
    }

    @Override
    public void disable() throws Exception {
        for (Map.Entry<String, ExternalAPI> entry : externalAPIs.entrySet()) {
            String pluginName = entry.getKey();
            ExternalAPI api = entry.getValue();

            try {
                api.disable();
            } catch (Exception ex) {
                ex.printStackTrace();
                main().getLogger().severe("Failed to load API support for [" + pluginName + "]");
            }
        }
    }

    /**
     * Register a new ExternalAPI support
     *
     * @param name  the name of the External Plugin's name
     * @param clazz the support class to support External Plugin
     * @return true if registered; false if replaced (which means plugin name duplicates)
     */
    public boolean registerExternalAPI(String name, Class<? extends ExternalAPI> clazz) {
        return apiClasses.put(name, clazz) == null;
    }

    public ExternalAPI getAPI(String pluginName) {
        return externalAPIs.get(pluginName);
    }

    public static abstract class ExternalAPI implements PluginRuntime {
        protected final PluginMain main;
        protected final String pluginName;

        public ExternalAPI(PluginMain main, String pluginName) {
            this.main = main;
            this.pluginName = pluginName;
        }

        public String getPluginName() {
            return pluginName;
        }
    }
}
