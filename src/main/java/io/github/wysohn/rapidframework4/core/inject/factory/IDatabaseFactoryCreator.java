package io.github.wysohn.rapidframework4.core.inject.factory;

import io.github.wysohn.rapidframework4.core.database.IDatabaseFactory;

public interface IDatabaseFactoryCreator {
    IDatabaseFactory create(String typeName);
}
