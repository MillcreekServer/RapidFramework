package io.github.wysohn.rapidframework3.core.database;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;

public class H2MemoryDatabase<T extends CachedElement<?>> extends H2Database<T> {
    public H2MemoryDatabase(Class<T> type,
                            String databaseName,
                            String userName,
                            String password) {
        super(type, "jdbc:h2:mem:" + databaseName, userName, password);
    }
}
