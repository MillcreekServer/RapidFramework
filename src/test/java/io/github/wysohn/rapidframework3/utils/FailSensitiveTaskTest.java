package io.github.wysohn.rapidframework3.utils;

import io.github.wysohn.rapidframework3.interfaces.IMemento;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class FailSensitiveTaskTest {

    @Test
    public void onFail() {
        Supplier mockRunnable = (Supplier<Boolean>) mock(Supplier.class);
        Runnable mockFailRunnable = mock(Runnable.class);

        doThrow(new RuntimeException()).when(mockRunnable).get();

        assertFalse(FailSensitiveTask.of(mockRunnable)
                .onFail(mockFailRunnable)
                .run());

        verify(mockRunnable).get();
        verify(mockFailRunnable).run();
    }

    @Test
    public void onFail2() {
        Supplier<Boolean> mockRunnable = (Supplier<Boolean>) mock(Supplier.class);
        Runnable mockFailRunnable = mock(Runnable.class);

        when(mockRunnable.get()).thenReturn(false);

        assertFalse(FailSensitiveTask.of(mockRunnable)
                .onFail(mockFailRunnable)
                .run());

        verify(mockRunnable).get();
        verify(mockFailRunnable).run();
    }

    @Test
    public void handleException() throws Exception {
        Supplier<Boolean> mockRunnable = (Supplier<Boolean>) mock(Supplier.class);

        doThrow(new RuntimeException()).when(mockRunnable).get();

        AtomicBoolean test = new AtomicBoolean();
        assertFalse(FailSensitiveTask.of(mockRunnable)
                .onFail(() -> {
                })
                .handleException(e -> test.set(true))
                .run());

        verify(mockRunnable).get();
        assertTrue(test.get());
    }

    @Test
    public void run() {
        Supplier<Boolean> mockRunnable = (Supplier<Boolean>) mock(Supplier.class);

        when(mockRunnable.get()).thenReturn(true);

        assertTrue(FailSensitiveTask.of(mockRunnable).run());

        verify(mockRunnable).get();
    }

    @Test
    public void mementoForgotConsumer() {
        Supplier<Boolean> mockRunnable = (Supplier<Boolean>) mock(Supplier.class);
        Consumer<Exception> exceptionConsumer = mock(Consumer.class);

        IMemento state1 = mock(IMemento.class);
        IMemento state2 = mock(IMemento.class);
        IMemento state3 = mock(IMemento.class);

        Consumer<IMemento> consumer1 = mock(Consumer.class);
        Consumer<IMemento> consumer2 = mock(Consumer.class);
        Consumer<IMemento> consumer3 = mock(Consumer.class);

        assertFalse(FailSensitiveTask.of(mockRunnable)
                .addStateSupplier("state1", () -> state1)
                .addStateSupplier("state2", () -> state2)
                .addStateSupplier("state3", () -> state3)
                .addStateConsumer("state1", consumer1)
                .addStateConsumer("state2", consumer2)
                .addStateConsumer("state1", consumer3)
                .handleException(exceptionConsumer)
                .run());

        verify(exceptionConsumer).accept(any(Exception.class));
    }

    @Test
    public void memento() {
        Supplier<Boolean> mockRunnable = (Supplier<Boolean>) mock(Supplier.class);
        Consumer<Exception> exceptionConsumer = mock(Consumer.class);
        doThrow(new RuntimeException()).when(mockRunnable).get();

        IMemento state1 = mock(IMemento.class);
        IMemento state2 = mock(IMemento.class);
        IMemento state3 = mock(IMemento.class);

        Consumer<IMemento> consumer1 = mock(Consumer.class);
        Consumer<IMemento> consumer2 = mock(Consumer.class);
        Consumer<IMemento> consumer3 = mock(Consumer.class);

        assertFalse(FailSensitiveTask.of(mockRunnable)
                .addStateSupplier("state1", () -> state1)
                .addStateSupplier("state2", () -> state2)
                .addStateSupplier("state3", () -> state3)
                .addStateConsumer("state1", consumer1)
                .addStateConsumer("state2", consumer2)
                .addStateConsumer("state3", consumer3)
                .handleException(exceptionConsumer)
                .run());

        verify(exceptionConsumer, times(1)).accept(any(Exception.class));
        verify(consumer1).accept(eq(state1));
        verify(consumer2).accept(eq(state2));
        verify(consumer3).accept(eq(state3));
    }
}