package io.github.wysohn.rapidframework2.manager.common;

public interface KeyValueStorage {
    <T> T get(String key);

    void put(String key, Object value);
}
