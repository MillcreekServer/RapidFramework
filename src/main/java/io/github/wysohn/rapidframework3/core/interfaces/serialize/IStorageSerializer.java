package io.github.wysohn.rapidframework3.core.interfaces.serialize;

import io.github.wysohn.rapidframework3.core.interfaces.store.permanent.ISavable;

public interface IStorageSerializer {
    String serializeToString(ISavable savable);
}
