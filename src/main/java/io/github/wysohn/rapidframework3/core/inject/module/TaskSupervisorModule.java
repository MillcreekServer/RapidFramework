package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework3.interfaces.plugin.ITaskSupervisor;

public class TaskSupervisorModule extends AbstractModule {
    private final ITaskSupervisor taskSupervisor;

    public TaskSupervisorModule(ITaskSupervisor taskSupervisor) {
        this.taskSupervisor = taskSupervisor;
    }

    @Provides
    @Singleton
    ITaskSupervisor getTaskSupervisor() {
        return taskSupervisor;
    }
}
