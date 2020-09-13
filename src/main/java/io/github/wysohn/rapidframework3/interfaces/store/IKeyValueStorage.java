package io.github.wysohn.rapidframework3.interfaces.store;

import java.io.File;
import java.util.Optional;
import java.util.Set;

public interface IKeyValueStorage {

    void reload(File file) throws Exception;

    /**
     * Get value relative to the root of this key/value storage.
     *
     * @param key the key
     * @param <T> return type
     * @return return value
     */
    <T> Optional<T> get(String key);

    /**
     * Get value relative to the specified section. Operation will fail
     * if 'section' is not a section indicated by {@link #isSection(Object)}.
     *
     * @param section the section. Must be true if evaluated with {@link #isSection(Object)}.
     * @param key     the key
     * @param <T>     return type
     * @return return value
     */
    <T> Optional<T> get(Object section, String key);

    /**
     * Put value to the path relative to the root.
     *
     * @param key   the key
     * @param value value to save
     */
    void put(String key, Object value);

    /**
     * Put value to the path relative to the root. Operation will fail
     * if 'section' is not a section indicated by {@link #isSection(Object)}.
     *
     * @param section the section. Must be true if evaluated with {@link #isSection(Object)}.
     * @param key     the key
     * @param value   value to save
     */
    void put(Object section, String key, Object value);

    /**
     * Get all keys belongs to the root
     *
     * @param deep true if to return all keys par to the leap nodes of tree; false to return only the direct children
     * @return set of keys in the root
     */
    Set<String> getKeys(boolean deep);

    /**
     * Get all keys belongs to the section.
     * if 'section' is not a section indicated by {@link #isSection(Object)}.
     *
     * @param section the section. Must be true if evaluated with {@link #isSection(Object)}.
     * @param deep    true if to return all keys par to the leap nodes of tree; false to return only the direct children
     * @return set of keys in the section
     */
    Set<String> getKeys(Object section, boolean deep);

    /**
     * Check whether the 'obj' is a section in the tree or not.
     *
     * @param obj object to test
     * @return true if it is a section; false otherwise
     */
    boolean isSection(Object obj);
}
