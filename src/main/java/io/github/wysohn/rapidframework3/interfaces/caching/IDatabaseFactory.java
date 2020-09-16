package io.github.wysohn.rapidframework3.interfaces.caching;

import io.github.wysohn.rapidframework3.core.database.Database;

public interface IDatabaseFactory {
    Database create(String tableName);
}
