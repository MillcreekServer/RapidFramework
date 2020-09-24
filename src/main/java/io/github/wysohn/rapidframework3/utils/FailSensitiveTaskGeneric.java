package io.github.wysohn.rapidframework3.utils;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FailSensitiveTaskGeneric<Task extends FailSensitiveTaskGeneric<?, ?>, T> {
    private final Supplier<T> task;
    private final T expected;

    private Runnable onFail;
    private Consumer<Exception> exceptionHandle = (ex) -> {
    };

    protected FailSensitiveTaskGeneric(Supplier<T> task, T expected) {
        this.task = task;
        this.expected = expected;
    }

    public Task onFail(Runnable task) {
        this.onFail = task;
        return (Task) this;
    }

    public Task handleException(Consumer<Exception> consumer) {
        this.exceptionHandle = consumer;
        return (Task) this;
    }

    public T run() {
        T result = null;
        try {
            result = task.get();
        } catch (Exception ex) {
            exceptionHandle.accept(ex);
        } finally {
            if (!Objects.equals(expected, result))
                onFail.run();
        }

        return result;
    }
}
