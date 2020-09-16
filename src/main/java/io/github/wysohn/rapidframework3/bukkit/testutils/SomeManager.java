package io.github.wysohn.rapidframework3.bukkit.testutils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework3.core.main.Manager;

@Singleton
public class SomeManager extends Manager {
    private boolean enable;
    private boolean load;
    private boolean disable;

    @Inject
    public SomeManager() {
        super();
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