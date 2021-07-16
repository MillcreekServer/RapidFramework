package io.github.wysohn.rapidframework3.core.database;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;

import java.io.IOException;
import java.util.Set;

public interface IDatabase<K, T extends CachedElement<K>> {
    String getTableName();

    T load(K key) throws IOException;

    void save(K key, T obj) throws IOException;

    /**
     * Check if the key exists in the database
     *
     * @param key the key to check
     * @return true if exists; false if not
     */
    boolean has(K key);

    /**
     * get list of all keys in this database. The operation time of this method can
     * be longer depends on the amount of data saved in the data. Make sure to use
     * it asynchronous manner or only once on initialization.
     *
     * @return
     */
    Set<K> getKeys();

    /**
     * Clear all data in the database. <b> Use it carefully as it will immediately
     * clear up the database</b>
     */
    void clear();
}
