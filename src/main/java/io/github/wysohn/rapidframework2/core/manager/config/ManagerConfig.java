package io.github.wysohn.rapidframework2.core.manager.config;

import io.github.wysohn.rapidframework2.core.interfaces.KeyValueStorage;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.common.AbstractFileSession;
import io.github.wysohn.rapidframework2.core.manager.common.Manager;

public class ManagerConfig extends Manager implements KeyValueStorage {
    private final AbstractFileSession fileSession;

    public ManagerConfig(PluginMain main, int loadPriority, AbstractFileSession fileSession) {
        super(main, loadPriority);

        this.fileSession = fileSession;
    }

    @Override
    public void enable() throws Exception {

    }

    @Override
    public void load() throws Exception {

    }

    @Override
    public void disable() throws Exception {

    }

    @Override
    public <T> T get(String key) {
        return fileSession.get(key);
    }

    @Override
    public void put(String key, Object value) {
        fileSession.put(key, value);
    }
}
