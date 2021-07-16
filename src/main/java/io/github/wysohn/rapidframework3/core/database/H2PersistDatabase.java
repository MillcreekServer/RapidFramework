package io.github.wysohn.rapidframework3.core.database;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;

public class H2PersistDatabase<K, T extends CachedElement<K>> extends H2Database<K, T> {
    public H2PersistDatabase(Class<T> type,
                             String filePath,
                             String userName,
                             String password) {
        super(type, "jdbc:h2:" + filePath, userName, password);
    }
}
