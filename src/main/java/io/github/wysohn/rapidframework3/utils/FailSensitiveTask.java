package io.github.wysohn.rapidframework3.utils;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FailSensitiveTask<T> {
    private final Supplier<T> task;

    private Runnable onFail;
    private Consumer<Exception> exceptionHandle = (ex) -> {
    };

    private FailSensitiveTask(Supplier<T> task) {
        this.task = task;
    }

    public static <T> FailSensitiveTask<T> of(Supplier<T> task) {
        return new FailSensitiveTask<T>(task);
    }

    public FailSensitiveTask<T> onFail(Runnable task) {
        this.onFail = task;
        return this;
    }

    public FailSensitiveTask<T> handleException(Consumer<Exception> consumer) {
        this.exceptionHandle = consumer;
        return this;
    }

    public T run(Predicate<T> fn) {
        T result = null;
        try {
            result = task.get();
        } catch (Exception ex) {
            exceptionHandle.accept(ex);
        } finally {
            if (!fn.test(result))
                onFail.run();
        }

        return result;
    }
}
