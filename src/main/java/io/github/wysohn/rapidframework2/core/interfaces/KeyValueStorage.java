package io.github.wysohn.rapidframework2.core.interfaces;

import io.github.wysohn.rapidframework2.core.interfaces.plugin.PluginRuntime;

import java.util.Set;

public interface KeyValueStorage {
    <T> T get(String key);

    void put(String key, Object value);

    Set<String> getKeys(boolean deep);

    boolean isSection(Object obj);

    void reload() throws Exception;
}
