package io.github.wysohn.rapidframework2.core.interfaces;

import java.util.Set;

public interface KeyValueStorage {
    <T> T get(String key);

    void put(String key, Object value);

    Set<String> getKeys(Boolean deep);

    boolean isSection(Object obj);
}
