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

import io.github.wysohn.rapidframework.database.tasks.DatabaseTransferTask.TransferPair;
import io.github.wysohn.rapidframework.pluginbase.manager.TransferPairProvider;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * expected to register it in {@link PluginManager}
 *
 * @param <T>
 * @author wysohn
 */
public abstract class PluginManager<T extends PluginBase> implements TransferPairProvider {
    public static final int FASTEST_PRIORITY = 0;
    public static final int NORM_PRIORITY = 5;
    public static final int SLOWEST_PRIORITY = 10;

    protected final T base;
    private final int loadPriority;

    private final ManagerConfig config;

    public PluginManager(T base, int loadPriority) {
        this.base = base;
        if (loadPriority < FASTEST_PRIORITY || loadPriority > SLOWEST_PRIORITY)
            throw new IllegalArgumentException("load priority out of bound.");

        this.loadPriority = loadPriority;

        File folder = new File(base.getDataFolder(), "config");
        this.config = initConfig(folder);
    }

    public int getLoadPriority() {
        return loadPriority;
    }

    void onInitInternal() throws Exception {
        if (this.config != null)
            this.config.onEnable(base);
    }

    void onDisableInternal() throws Exception {
        if (this.config != null)
            this.config.onDisable(base);
    }

    void onReloadInternal() throws Exception {
        if (this.config != null)
            this.config.onReload(base);
    }

    /**
     * Initialize manager specific config. You may extend {@link ManagerConfig} to
     * create a custom config type. Override this method to use custom config;
     * otherwise, no manger specific config will be used.
     *
     * @param folder
     * @return the ManagerConfig to be used as config of this manager instance.
     */
    protected ManagerConfig initConfig(File folder) {
        return new ManagerConfig();
    }

    /**
     * Get general information about this manager. You may override this method to
     * provided information to be shown when a player enter /somecommand status.
     *
     * @return The map containing information; null if nothing to show.
     */
    protected Map<String, Object> getInfo() {
        return null;
    }

    protected abstract void onEnable() throws Exception;

    protected abstract void onDisable() throws Exception;

    protected abstract void onReload() throws Exception;

    public <C extends ManagerConfig> C getManagerConfig() {
        return (C) config;
    }

    @Override
    public Set<String> getValidDBTypes() {
        return null;
    }

    @Override
    public Set<TransferPair> getTransferPair(String dbTypeFrom) {
        return null;
    }

    /**
     * Look {@link ConfigBase} for details
     *
     * @author wysohn
     */
    protected class ManagerConfig extends ConfigBase {

        /**
         * Do not override this method unless you want to use very specific folder. This
         * method is already overriden by ManagerConfig and will create appropriate
         * config file without extra works.
         */
        @Override
        protected File initConfigFile(PluginBase base) {
            File folder = new File(base.getDataFolder(), "config");
            return new File(folder, PluginManager.this.getClass().getSimpleName() + ".yml");
        }
    }
}
