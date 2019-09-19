package io.github.wysohn.rapidframework2.manager.config;

import io.github.wysohn.rapidframework2.main.PluginMain;
import io.github.wysohn.rapidframework2.manager.Manager;
import io.github.wysohn.rapidframework2.manager.common.AbstractFileSession;

public class ManagerConfig extends Manager {
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
}
