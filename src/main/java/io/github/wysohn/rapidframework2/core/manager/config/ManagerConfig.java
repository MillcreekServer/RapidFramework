package io.github.wysohn.rapidframework2.core.manager.config;

import io.github.wysohn.rapidframework2.core.interfaces.KeyValueStorage;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.common.AbstractFileSession;

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
    public void put(String key, Object value) {
        fileSession.put(key, value);
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return null;
    }

    @Override
    public boolean isSection(Object obj) {
        return false;
    }

    @Override
    public void reload() {

    }
}
