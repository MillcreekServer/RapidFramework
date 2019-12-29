package io.github.wysohn.rapidframework2.core.interfaces;

import java.util.Optional;
import java.util.Set;

public interface KeyValueStorage {
    <T> Optional<T> get(String key);

    void put(String key, Object value);

    Set<String> getKeys(boolean deep);

    boolean isSection(Object obj);

    void reload() throws Exception;
}
