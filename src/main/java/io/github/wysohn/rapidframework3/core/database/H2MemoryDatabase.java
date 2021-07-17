package io.github.wysohn.rapidframework3.core.database;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;

public class H2MemoryDatabase<K, T extends CachedElement<K>> extends H2Database<K, T> {
    public H2MemoryDatabase(Class<T> type,
                            String databaseName) {
        super(type, "jdbc:h2:mem:" + databaseName, "memory", "memory");
    }
}
