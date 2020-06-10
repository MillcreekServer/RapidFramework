package io.github.wysohn.rapidframework2.core.manager.common;

import io.github.wysohn.rapidframework2.core.interfaces.KeyValueStorage;

import java.io.File;
import java.util.Optional;
import java.util.Set;

public class KeyValueStorageAdapter implements KeyValueStorage {
    @Override
    public <T> Optional<T> get(String key) {
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> get(Object section, String key) {
        return Optional.empty();
    }

    @Override
    public void put(String key, Object value) {

    }

    @Override
    public void put(Object section, String key, Object value) {

    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return null;
    }

    @Override
    public Set<String> getKeys(Object section, boolean deep) {
        return null;
    }

    @Override
    public boolean isSection(Object obj) {
        return false;
    }

    @Override
    public void reload() throws Exception {

    }

    @Override
    public void save() {

    }

    @Override
    public void save(File saveTo) {

    }
}
