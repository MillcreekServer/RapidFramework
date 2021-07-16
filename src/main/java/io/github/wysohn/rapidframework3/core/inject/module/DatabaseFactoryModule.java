package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import io.github.wysohn.rapidframework3.core.database.DatabaseFactory;
import io.github.wysohn.rapidframework3.core.database.IDatabaseFactory;
import io.github.wysohn.rapidframework3.core.inject.factory.IDatabaseFactoryCreator;

public class DatabaseFactoryModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
            .implement(IDatabaseFactory.class, DatabaseFactory.class)
            .build(IDatabaseFactoryCreator.class));
    }
}
