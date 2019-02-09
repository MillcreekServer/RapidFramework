/*******************************************************************************
 *     Copyright (C) 2017 wysohn
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package io.github.wysohn.rapidframework.pluginbase;

import io.github.wysohn.rapidframework.pluginbase.api.JsonApiAPI;
import io.github.wysohn.rapidframework.pluginbase.api.VaultAPI;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public final class PluginAPISupport implements PluginProcedure {
    private final Queue<Runnable> hookQueue = new LinkedList<Runnable>();
    final Map<String, APISupport> apis = new HashMap<String, APISupport>();

    public PluginAPISupport() {
        hookAPI("PlaceholderAPI");
        hookAPI("UserInterfaceLib");
        hookAPI("Vault", VaultAPI.class);
        hookAPI("JsonApi", JsonApiAPI.class);
    }

    private PluginBase base;

    @Override
    public void onEnable(PluginBase base) throws Exception {
        this.base = base;

        while (!hookQueue.isEmpty()) {
            Runnable run = hookQueue.poll();

            run.run();
        }
    }

    @Override
    public void onDisable(PluginBase base) throws Exception {

    }

    @Override
    public void onReload(PluginBase base) throws Exception {

    }

    /**
     *
     * @param pluginName
     */
    public void hookAPI(final String pluginName, final Class<? extends APISupport> api) {
        hookQueue.add(new Runnable() {
            @Override
            public void run() {
                PluginManager pm = base.getServer().getPluginManager();

                Plugin plugin = pm.getPlugin(pluginName);
                if (plugin != null && plugin.isEnabled()) {
                    base.getLogger().info("Hooked Plugin [" + pluginName + "]");
                    base.getLogger().info("Info: " + plugin.getDescription().getFullName());

                    if (api != null) {
                        try {
                            Constructor con = api.getConstructor(PluginBase.class);
                            APISupport supp = (APISupport) con.newInstance(base);
                            supp.pluginName = pluginName;

                            if(supp.init()){
                                pm.registerEvents(supp, base);
                                apis.put(pluginName, supp);
                            }
                        } catch (Exception e) {
                            base.getLogger().severe("Failed to initialize [" + pluginName + "]");
                            base.getLogger().severe(e.getMessage());
                        }
                    } else {
                        apis.put(pluginName, new APISupport(base) {
                            @Override
                            public boolean init() throws Exception {
                                return true;
                            }
                        });
                    }
                }
            }
        });
    }

    public void hookAPI(final String pluginName) {
        hookAPI(pluginName, null);
    }

    public boolean isExist(String pluginName) {
        return base.getServer().getPluginManager().getPlugin(pluginName) != null;
    }
    
    public void assertAPI(String pluginName) {
    	if(base.getServer().getPluginManager().getPlugin(pluginName) == null)
    		throw new RuntimeException(pluginName+ " is not enabled!");
    }

    /**
     *
     * @param pluginName
     * @return check whether the api is registered or not
     */
    public boolean isHooked(String pluginName) {
        return apis.containsKey(pluginName);
    }

    /**
     *
     * @param pluginName
     * @return
     */
    public <T extends APISupport> T getAPI(String pluginName) {
        return (T) apis.get(pluginName);
    }

    public static abstract class APISupport implements Listener {
        protected final PluginBase base;
        private String pluginName;

        public APISupport(PluginBase base) {
            this.base = base;
        }

        public String getPluginName() {
			return pluginName;
		}

		public abstract boolean init() throws Exception;

    }
}
