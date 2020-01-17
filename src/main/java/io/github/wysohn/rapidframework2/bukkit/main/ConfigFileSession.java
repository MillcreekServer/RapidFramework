package io.github.wysohn.rapidframework2.bukkit.main;

import io.github.wysohn.rapidframework2.core.manager.common.AbstractFileSession;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class ConfigFileSession extends AbstractFileSession {
    private FileConfiguration config;

    public ConfigFileSession(File file) {
        super(file);

        config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void reload() throws IOException {
        config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void save() throws IOException {
        new Thread(()->{
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public <T> Optional<T> get(String key) {
        T value = (T) config.get(key, null);
        if(value == null)
            return Optional.empty();
        else
            return Optional.of(value);
    }

    @Override
    public void put(String key, Object value) {
        config.set(key, value);
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return config.getKeys(false);
    }

    @Override
    public boolean isSection(Object obj) {
        return obj instanceof ConfigurationSection;
    }
}
