package io.github.wysohn.rapidframework3.core.main;

import io.github.wysohn.rapidframework3.core.inject.annotations.PluginConfig;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;
import io.github.wysohn.rapidframework3.core.interfaces.io.file.IFileReader;
import io.github.wysohn.rapidframework3.core.interfaces.io.file.IFileWriter;
import io.github.wysohn.rapidframework3.core.interfaces.serialize.IStorageSerializer;
import io.github.wysohn.rapidframework3.core.interfaces.store.temporary.IKeyValueStorage;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ManagerConfig extends Manager implements IKeyValueStorage {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final IFileReader fileReader;
    private final IFileWriter fileWriter;
    private final File pluginDirectory;
    private final IStorageSerializer serializer;
    private final IKeyValueStorage storage;

    @Inject
    public ManagerConfig(PluginMain main,
                         IFileReader fileReader,
                         IFileWriter fileWriter,
                         @PluginDirectory File pluginDirectory,
                         @PluginConfig IStorageSerializer serializer,
                         @PluginConfig IKeyValueStorage storage) {
        super(main);
        this.fileReader = fileReader;
        this.fileWriter = fileWriter;
        this.pluginDirectory = pluginDirectory;
        this.serializer = serializer;
        this.storage = storage;
    }

    private File safeGetConfigFile() {
        if (!pluginDirectory.exists())
            pluginDirectory.mkdirs();

        File file = new File(pluginDirectory, "config.yml");
        return file;
    }

    @Override
    public void enable() throws Exception {

    }

    @Override
    public void load() throws Exception {
        File file = safeGetConfigFile();
        if (!file.exists()) return;

        String serializedStr = fileReader.apply(file);
        restoreFromString(serializedStr);
    }

    private void storeCurrentState() {
        File file = safeGetConfigFile();

        String serializedStr = storeAsString();
        executor.execute(() -> {
            try {
                fileWriter.accept(file, serializedStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void disable() throws Exception {
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Override
    public <T> Optional<T> get(String key) {
        return storage.get(key);
    }

    @Override
    public <T> Optional<T> get(Object section, String key) {
        if (!isSection(section))
            throw new RuntimeException(section + " is not a section.");

        return storage.get(section, key);
    }

    @Override
    public void put(String key, Object value) {
        storage.put(key, value);

        storeCurrentState();
    }

    @Override
    public void put(Object section, String key, Object value) {
        if (!isSection(section))
            throw new RuntimeException(section + " is not a section.");

        storage.put(section, key, value);
        storeCurrentState();
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return storage.getKeys(deep);
    }

    @Override
    public Set<String> getKeys(Object section, boolean deep) {
        if (!isSection(section))
            throw new RuntimeException(section + " is not a section.");

        return storage.getKeys(section, false);
    }

    @Override
    public boolean isSection(Object obj) {
        return storage.isSection(obj);
    }

    @Override
    public void restoreFromString(String data) {
        storage.restoreFromString(data);
    }

    @Override
    public String storeAsString() {
        return serializer.serializeToString(this);
    }
}
