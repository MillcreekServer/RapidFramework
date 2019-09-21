package io.github.wysohn.rapidframework2.core.manager.command;

import io.github.wysohn.rapidframework.pluginbase.language.DefaultLanguages;
import io.github.wysohn.rapidframework2.core.main.PluginMain;

public final class ManagerCommand extends PluginMain.Manager {
    private final String mainCommand;
    private String defaultCommand = "help";
    private SubCommandMap commandMap;

    public ManagerCommand(int loadPriority, String mainCommand) {
        super(loadPriority);
        this.mainCommand = mainCommand;
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
