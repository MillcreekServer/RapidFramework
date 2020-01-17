package io.github.wysohn.rapidframework2.core.main;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.TaskSupervisor;

import java.util.function.Consumer;

public interface PluginBridge {
    TaskSupervisor getTaskSupervisor();

    void forEachPlayer(Consumer<ICommandSender> consumer);
}
