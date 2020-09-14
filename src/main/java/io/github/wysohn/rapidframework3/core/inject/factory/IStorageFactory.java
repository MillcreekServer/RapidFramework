package io.github.wysohn.rapidframework3.core.inject.factory;

import io.github.wysohn.rapidframework3.interfaces.store.IKeyValueStorage;

import java.io.File;

public interface IStorageFactory {
    IKeyValueStorage create(File directory, String fileName);
}
