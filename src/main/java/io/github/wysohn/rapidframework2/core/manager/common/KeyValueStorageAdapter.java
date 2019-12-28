package io.github.wysohn.rapidframework2.core.manager.common;

import io.github.wysohn.rapidframework2.core.interfaces.KeyValueStorage;

import java.util.Set;

public class KeyValueStorageAdapter implements KeyValueStorage {
    @Override
    public <T> T get(String key) {
        return null;
    }

    @Override
    public void put(String key, Object value) {

    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return null;
    }

    @Override
    public boolean isSection(Object obj) {
        return false;
    }

    @Override
    public void reload() {

    }
}
