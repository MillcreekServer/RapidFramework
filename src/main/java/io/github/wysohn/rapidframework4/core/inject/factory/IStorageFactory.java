package io.github.wysohn.rapidframework4.core.inject.factory;

import io.github.wysohn.rapidframework4.interfaces.store.IKeyValueStorage;

import java.io.File;

public interface IStorageFactory {
    /**
     * Create a savable key-value pair, persistent storage using whatever supported storage type.
     * In Bukkit API, for example, it would be YamlConfiguration.
     *
     * @param directory directory to save the storage
     * @param fileName  the full file name, including the extension
     * @return the storage
     */
    IKeyValueStorage create(File directory, String fileName);
}
