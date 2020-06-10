package io.github.wysohn.rapidframework2.core.manager.config;

import io.github.wysohn.rapidframework2.core.interfaces.KeyValueStorage;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.common.AbstractFileSession;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class ManagerConfig extends PluginMain.Manager implements KeyValueStorage {
    private final AbstractFileSession fileSession;

    public ManagerConfig(int loadPriority, AbstractFileSession fileSession) {
        super(loadPriority);

        this.fileSession = fileSession;
    }

    @Override
    public void enable() throws Exception {

    }

    @Override
    public void load() throws Exception {
        reload();
    }

    @Override
    public void disable() throws Exception {

    }

    @Override
    public <T> Optional<T> get(String key) {
        return fileSession.get(key);
    }

    @Override
    public <T> Optional<T> get(Object section, String key) {
        if(!isSection(section))
            throw new RuntimeException(section+" is not a section.");

        return fileSession.get(section, key);
    }

    @Override
    public void put(String key, Object value) {
        fileSession.put(key, value);
    }

    @Override
    public void put(Object section, String key, Object value) {
        if(!isSection(section))
            throw new RuntimeException(section+" is not a section.");

        fileSession.put(section, key, value);
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return fileSession.getKeys(deep);
    }

    @Override
    public Set<String> getKeys(Object section, boolean deep) {
        if(!isSection(section))
            throw new RuntimeException(section+" is not a section.");

        return fileSession.getKeys(section, false);
    }

    @Override
    public boolean isSection(Object obj) {
        return fileSession.isSection(obj);
    }

    @Override
    public void reload() {
        try {
            fileSession.reload();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        fileSession.save();
    }

    @Override
    public void save(File saveTo) {
        fileSession.save(saveTo);
    }
}
