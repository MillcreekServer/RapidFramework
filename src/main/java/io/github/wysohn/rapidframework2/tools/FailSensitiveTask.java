package io.github.wysohn.rapidframework2.tools;

import java.util.function.Consumer;

public class FailSensitiveTask {
    private final Runnable task;

    private Runnable onFail;
    private Consumer<Exception> exceptionHandle = (ex) -> {
    };

    private FailSensitiveTask(Runnable runnable) {
        this.task = runnable;
    }

    public static FailSensitiveTask of(Runnable task) {
        return new FailSensitiveTask(task);
    }

    public FailSensitiveTask onFail(Runnable task) {
        this.onFail = task;
        return this;
    }

    public FailSensitiveTask handleException(Consumer<Exception> consumer) {
        this.exceptionHandle = consumer;
        return this;
    }

    public void run() {
        try {
            task.run();
        } catch (Exception ex) {
            onFail.run();
            exceptionHandle.accept(ex);
        }
    }
}
