package io.github.wysohn.rapidframework4.interfaces.caching;

import io.github.wysohn.rapidframework4.core.database.Database;

public interface IDatabaseFactory {
    Database create(String tableName);
}
