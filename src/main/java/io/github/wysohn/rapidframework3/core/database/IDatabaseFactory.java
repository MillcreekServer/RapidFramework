package io.github.wysohn.rapidframework3.core.database;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;

import java.util.function.Function;

public interface IDatabaseFactory {
    <K, V extends CachedElement<K>> IDatabase<K, V> create(String tableName,
                                                           Class<V> valueType,
                                                           Function<String, K> strToKey);
}
