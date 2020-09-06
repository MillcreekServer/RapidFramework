package io.github.wysohn.rapidframework2.core.database.sqlite;

import io.github.wysohn.rapidframework2.core.database.Database;

import java.util.Set;

public class DatabaseSqlite<T> extends Database<T> {

    public DatabaseSqlite(Class<T> type, String tableName) {
        super(type, tableName);
    }

    @Override
    public T load(String key, T def) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void save(String key, T value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveSerializedString(String key, String serialized) {

    }

    @Override
    public boolean has(String key) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Set<String> getKeys() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

}
