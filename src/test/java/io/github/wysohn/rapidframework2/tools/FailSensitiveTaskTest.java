package io.github.wysohn.rapidframework2.tools;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class FailSensitiveTaskTest {

    @Test
    public void onFail() {
        Runnable mockRunnable = mock(Runnable.class);
        Runnable mockFailRunnable = mock(Runnable.class);

        doThrow(new RuntimeException()).when(mockRunnable).run();

        FailSensitiveTask.of(mockRunnable)
                .onFail(mockFailRunnable)
                .run();

        verify(mockRunnable).run();
        verify(mockFailRunnable).run();
    }

    @Test
    public void handleException() throws Exception {
        Runnable mockRunnable = mock(Runnable.class);

        doThrow(new RuntimeException()).when(mockRunnable).run();

        AtomicBoolean test = new AtomicBoolean();
        FailSensitiveTask.of(mockRunnable)
                .onFail(() -> {
                })
                .handleException(e -> test.set(true))
                .run();

        verify(mockRunnable).run();
        assertTrue(test.get());
    }

    @Test
    public void run() {
        Runnable mockRunnable = mock(Runnable.class);

        FailSensitiveTask.of(mockRunnable)
                .run();

        verify(mockRunnable).run();
    }
}