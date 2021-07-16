package io.github.wysohn.rapidframework3.core.inject.factory;

import io.github.wysohn.rapidframework3.core.database.IDatabaseFactory;

public interface IDatabaseFactoryCreator {
    IDatabaseFactory create(String typeName);
}
