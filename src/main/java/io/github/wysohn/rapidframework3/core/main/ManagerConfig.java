package io.github.wysohn.rapidframework3.core.main;

import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;
import io.github.wysohn.rapidframework3.core.inject.factory.IStorageFactory;
import io.github.wysohn.rapidframework3.interfaces.store.IKeyValueStorage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Singleton
public class ManagerConfig extends Manager {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final File pluginDirectory;
    private final IKeyValueStorage storage;

    @Inject
    public ManagerConfig(PluginMain main,
                         @PluginDirectory File pluginDirectory,
                         IStorageFactory storageFactory) {
        super(main);
        this.pluginDirectory = pluginDirectory;
        this.storage = storageFactory.create(pluginDirectory, "config.yml");
    }

    @Override
    public void enable() throws Exception {

    }

    @Override
    public void load() throws Exception {
        storage.reload();
    }

    @Override
    public void disable() throws Exception {
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    public <T> Optional<T> get(String key) {
        return storage.get(key);
    }

    public <T> Optional<T> get(Object section, String key) {
        if (!isSection(section))
            throw new RuntimeException(section + " is not a section.");

        return storage.get(section, key);
    }

    public void put(String key, Object value) {
        storage.put(key, value);
    }

    public void put(Object section, String key, Object value) {
        if (!isSection(section))
            throw new RuntimeException(section + " is not a section.");

        storage.put(section, key, value);
    }

    public Set<String> getKeys(boolean deep) {
        return storage.getKeys(deep);
    }

    public Set<String> getKeys(Object section, boolean deep) {
        if (!isSection(section))
            throw new RuntimeException(section + " is not a section.");

        return storage.getKeys(section, false);
    }

    public boolean isSection(Object obj) {
        return storage.isSection(obj);
    }
}
