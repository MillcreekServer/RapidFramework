package io.github.wysohn.rapidframework3.core.database;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class HibernateDatabase<T extends CachedElement<?>> extends Database<T> {
    private final Properties properties;

    public HibernateDatabase(ISerializer serializer,
                             String tableName, Class<T> type, Properties properties) {
        super(serializer, tableName, type);
        this.properties = properties;
    }

    @Override
    public T load(String key) throws IOException {
        return null;
    }

    @Override
    public void save(String key, T obj) throws IOException {

    }

    @Override
    public boolean has(String key) {
        return false;
    }

    @Override
    public Set<String> getKeys() {
        return null;
    }

    @Override
    public void clear() {

    }


}
