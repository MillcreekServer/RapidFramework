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

import java.util.Set;

/**
 * expected to register it in {@link PluginManager}
 * 
 * @author wysohn
 *
 * @param <T>
 */
public abstract class PluginManager<T extends PluginBase> implements TransferPairProvider {
    public static final int FASTEST_PRIORITY = 0;
    public static final int NORM_PRIORITY = 5;
    public static final int SLOWEST_PRIORITY = 10;

    protected final T base;
    private final int loadPriority;

    public PluginManager(T base, int loadPriority) {
        this.base = base;
        if (loadPriority < FASTEST_PRIORITY || loadPriority > SLOWEST_PRIORITY)
            throw new IllegalArgumentException("load priority out of bound.");

        this.loadPriority = loadPriority;
        /*
         * base.pluginManagers.put(this.getClass(), this);
         */
    }

    public int getLoadPriority() {
        return loadPriority;
    }

    protected abstract void onEnable() throws Exception;

    protected abstract void onDisable() throws Exception;

    protected abstract void onReload() throws Exception;

    @Override
    public Set<String> getValidDBTypes() {
        return null;
    }

    @Override
    public Set<TransferPair> getTransferPair(String dbTypeFrom) {
        return null;
    }

}
