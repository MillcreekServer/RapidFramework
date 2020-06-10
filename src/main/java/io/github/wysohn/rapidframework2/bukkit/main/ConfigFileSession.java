package io.github.wysohn.rapidframework2.bukkit.main;

import io.github.wysohn.rapidframework2.bukkit.utils.Utf8YamlConfiguration;
import io.github.wysohn.rapidframework2.core.manager.common.AbstractFileSession;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class ConfigFileSession extends AbstractFileSession {
    private YamlConfiguration config;

    public ConfigFileSession(File file) {
        super(file);

        config = Utf8YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void reload() throws IOException {
        config = Utf8YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void save() {
        save(file);
    }

    @Override
    public void save(File saveTo) {
        new Thread(() -> {
            try {
                config.save(saveTo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public <T> Optional<T> get(String key) {
        T value = (T) config.get(key, null);
        return Optional.ofNullable(value);
    }

    @Override
    public <T> Optional<T> get(Object section, String key) {
        if(!isSection(section))
            throw new RuntimeException(section+" is not a section.");

        ConfigurationSection s = (ConfigurationSection) section;

        T value = (T) s.get(key, null);
        return Optional.ofNullable(value);
    }

    @Override
    public void put(String key, Object value) {
        config.set(key, value);
    }

    @Override
    public void put(Object section, String key, Object value) {
        if(!isSection(section))
            throw new RuntimeException(section+" is not a section.");

        ConfigurationSection s = (ConfigurationSection) section;

        s.set(key, value);
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return config.getKeys(deep);
    }

    @Override
    public Set<String> getKeys(Object section, boolean deep) {
        if(!isSection(section))
            throw new RuntimeException(section+" is not a section.");

        ConfigurationSection s = (ConfigurationSection) section;

        return s.getKeys(deep);
    }

    @Override
    public boolean isSection(Object obj) {
        return obj instanceof ConfigurationSection;
    }
}
