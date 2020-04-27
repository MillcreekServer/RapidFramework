package io.github.wysohn.rapidframework2.tools;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
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
}