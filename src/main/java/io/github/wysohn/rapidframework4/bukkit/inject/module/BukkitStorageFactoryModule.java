package io.github.wysohn.rapidframework4.bukkit.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import io.github.wysohn.rapidframework4.bukkit.config.BukkitKeyValueStorage;
import io.github.wysohn.rapidframework4.core.inject.factory.IStorageFactory;
import io.github.wysohn.rapidframework4.interfaces.store.IKeyValueStorage;

public class BukkitStorageFactoryModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(IKeyValueStorage.class, BukkitKeyValueStorage.class)
                .build(IStorageFactory.class));
    }
}
