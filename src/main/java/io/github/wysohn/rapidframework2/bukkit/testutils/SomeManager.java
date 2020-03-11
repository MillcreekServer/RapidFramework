package io.github.wysohn.rapidframework2.bukkit.testutils;

import io.github.wysohn.rapidframework2.core.main.PluginMain;

public class SomeManager extends PluginMain.Manager {
    private boolean enable;
    private boolean load;
    private boolean disable;

    public SomeManager(int loadPriority) {
        super(loadPriority);
    }

    @Override
    public void enable() throws Exception {
        enable = true;
    }

    @Override
    public void load() throws Exception {
        load = true;
    }

    @Override
    public void disable() throws Exception {
        disable = true;
    }

    public boolean isEnable() {
        return enable;
    }

    public boolean isLoad() {
        return load;
    }

    public boolean isDisable() {
        return disable;
    }

    public void reset() {
        enable = false;
        load = false;
        disable = false;
    }
}