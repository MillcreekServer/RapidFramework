package io.github.wysohn.rapidframework4.utils;

import io.github.wysohn.rapidframework4.interfaces.IMemento;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FailSensitiveTaskGeneric<Task extends FailSensitiveTaskGeneric<?, ?>, T> {
    private final Supplier<T> task;
    private final T expected;
    private final Map<String, Supplier<IMemento>> mementoSuppliers = new HashMap<>();
    private final Map<String, Consumer<IMemento>> mementoConsumers = new HashMap<>();
    private final Map<String, IMemento> savedStates = new HashMap<>();

    private Runnable onFail;
    private Consumer<Exception> exceptionHandle = (ex) -> {
    };

    protected FailSensitiveTaskGeneric(Supplier<T> task, T expected) {
        this.task = task;
        this.expected = expected;
    }

    /**
     * Add a IMemento supplier to be used to save the state associated with the given
     * 'key.' Later, if any of the fail sensitive task fail, the state will be restored
     * to the saved version of IMemento, provided by the supplier.
     *
     * @param key             any String to be used as identifier
     * @param mementoSupplier memento supplier
     * @return this instance for builder pattern
     */
    public Task addStateSupplier(String key, Supplier<IMemento> mementoSupplier) {
        mementoSuppliers.put(key, mementoSupplier);
        return (Task) this;
    }

    /**
     * Add a IMemento consumer to be used to restore the saved state associated with
     * the given 'key.' If something went wrong while executing the fail sensitive task,
     * the states saved using the supplier provided in {@link #addStateSupplier(String, Supplier)}
     * will be used to attempt to revert the state to the last safe point. The consumer
     * provided here will do that if and only if the saved state exist for the associated
     * key.
     *
     * @param key             the key used for the memento supplier
     * @param mementoConsumer the consumer to consume the memento
     * @return this instance for builder pattern
     */
    public Task addStateConsumer(String key, Consumer<IMemento> mementoConsumer) {
        mementoConsumers.put(key, mementoConsumer);
        return (Task) this;
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
            // verify that memento suppliers matches with memento consumers
            // fail early before we get into a further trouble
            for (String key : mementoSuppliers.keySet()) {
                if (!mementoConsumers.containsKey(key))
                    throw new Exception("Consumer not defined for " + key);
            }

            // save states before executing the task
            mementoSuppliers.forEach((key, supplier) ->
                    savedStates.put(key, Objects.requireNonNull(supplier.get())));
        } catch (Exception ex) {
            Optional.ofNullable(exceptionHandle).ifPresent(handle -> handle.accept(ex));
            Optional.ofNullable(onFail).ifPresent(Runnable::run);
            return null;
        }

        try {
            // execute the task
            result = task.get();
        } catch (Exception ex) {
            Optional.ofNullable(exceptionHandle).ifPresent(handle -> handle.accept(ex));
        } finally {
            if (!Objects.equals(expected, result)) {
                // let user code run first to minimize the impact
                Optional.ofNullable(onFail).ifPresent(Runnable::run);

                // restore state
                mementoConsumers.forEach((key, consumer) ->
                        Optional.ofNullable(savedStates.get(key)).ifPresent(consumer));
            }
        }

        return result;
    }
}
