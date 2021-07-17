package io.github.wysohn.rapidframework4.bukkit.config;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.github.wysohn.rapidframework4.bukkit.utils.Utf8YamlConfiguration;
import io.github.wysohn.rapidframework4.interfaces.io.file.IFileWriter;
import io.github.wysohn.rapidframework4.interfaces.store.IKeyValueStorage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BukkitKeyValueStorage implements IKeyValueStorage {
    private final IFileWriter writer;
    private final File file;

    private ExecutorService exec = Executors.newSingleThreadExecutor();
    private FileConfiguration config = new Utf8YamlConfiguration();

    @Inject
    public BukkitKeyValueStorage(IFileWriter writer,
                                 @Assisted File directory,
                                 @Assisted String fileName) throws Exception {
        this.writer = writer;
        this.file = IKeyValueStorage.safeGetFile(directory, fileName);

        reload();
    }

    @Override
    public void reload() throws Exception {
        exec.shutdown();
        exec.awaitTermination(30, TimeUnit.SECONDS);

        exec = Executors.newSingleThreadExecutor();
        config = Utf8YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void save() throws Exception {
        //TODO not necessary
    }

    @Override
    public <T> Optional<T> get(String key) {
        T value = (T) config.get(key, null);
        return Optional.ofNullable(value);
    }

    @Override
    public <T> Optional<T> get(Object section, String key) {
        if (!isSection(section))
            throw new RuntimeException(section + " is not a section.");

        ConfigurationSection s = (ConfigurationSection) section;

        T value = (T) s.get(key, null);
        return Optional.ofNullable(value);
    }

    @Override
    public void put(String key, Object value) {
        config.set(key, value);

        saveState();
    }

    @Override
    public void put(Object section, String key, Object value) {
        if (!isSection(section))
            throw new RuntimeException(section + " is not a section.");

        ConfigurationSection s = (ConfigurationSection) section;

        s.set(key, value);

        saveState();
    }

    private void saveState() {
        String serialized = config.saveToString();
        exec.execute(() -> {
            try {
                writer.accept(file, serialized);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return config.getKeys(deep);
    }

    @Override
    public Set<String> getKeys(Object section, boolean deep) {
        if (!isSection(section))
            throw new RuntimeException(section + " is not a section.");

        ConfigurationSection s = (ConfigurationSection) section;

        return s.getKeys(deep);
    }

    @Override
    public boolean isSection(Object obj) {
        return obj instanceof ConfigurationSection;
    }

    public void shutdown() {
        try {
            exec.shutdown();
            exec.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return file.toString();
    }
}
