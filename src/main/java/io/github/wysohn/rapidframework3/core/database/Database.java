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
package io.github.wysohn.rapidframework3.core.database;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;

import java.io.IOException;
import java.util.Set;

public abstract class Database<T extends CachedElement<?>> {
    protected final ISerializer serializer;
    protected final String tableName;
    protected final Class<T> type;

    public Database(ISerializer serializer, String tableName, Class<T> type) {
        this.serializer = serializer;
        this.tableName = tableName;
        this.type = type;
    }

    public String getTableName() {
        return tableName;
    }

    public abstract T load(String key) throws IOException;

    public abstract void save(String key, T obj) throws IOException;

    /**
     * Check if the key exists in the database
     *
     * @param key the key to check
     * @return true if exists; false if not
     */
    public abstract boolean has(String key);

    /**
     * get list of all keys in this database. The operation time of this method can
     * be longer depends on the amount of data saved in the data. Make sure to use
     * it asynchronous manner or only once on initialization.
     *
     * @return
     */
    public abstract Set<String> getKeys();

    /**
     * Clear all data in the database. <b> Use it carefully as it will immediately
     * clear up the database</b>
     */
    public abstract void clear();
}
