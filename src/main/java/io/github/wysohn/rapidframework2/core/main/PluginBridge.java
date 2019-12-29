package io.github.wysohn.rapidframework2.core.main;

import io.github.wysohn.rapidframework2.core.interfaces.plugin.TaskSupervisor;

public interface PluginBridge {
    TaskSupervisor getTaskSupervisor();
}
