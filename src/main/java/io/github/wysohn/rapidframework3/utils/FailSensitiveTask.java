package io.github.wysohn.rapidframework3.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FailSensitiveTask {
    private final Supplier<Boolean> task;

    private Runnable onFail;
    private Consumer<Exception> exceptionHandle = (ex) -> {
    };

    private FailSensitiveTask(Supplier<Boolean> task) {
        this.task = task;
    }

    public static FailSensitiveTask of(Supplier<Boolean> task) {
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

    public boolean run() {
        boolean result = false;
        try {
            result = task.get();
        } catch (Exception ex) {
            exceptionHandle.accept(ex);
        } finally {
            if (!result)
                onFail.run();
        }

        return result;
    }
}
