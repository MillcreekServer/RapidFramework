package io.github.wysohn.rapidframework3.utils;

import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DoubleCheckerTest {
    @Test
    public void init() {
        DoubleChecker doubleChecker = new DoubleChecker();

        UUID uuid = UUID.randomUUID();

        doubleChecker.init(uuid, () -> {
        }, () -> {
        });

        Map taskMap = (Map) Whitebox.getInternalState(doubleChecker, "nextTasks");
        assertTrue(taskMap.containsKey(uuid));
    }

    @Test
    public void confirm() {
        DoubleChecker doubleChecker = new DoubleChecker();

        UUID uuid = UUID.randomUUID();
        Runnable mockRunnable = mock(Runnable.class);
        Runnable mockRunnableTimeout = mock(Runnable.class);

        doubleChecker.init(uuid, mockRunnable, mockRunnableTimeout);
        doubleChecker.confirm(uuid);

        verify(mockRunnable).run();
        verify(mockRunnableTimeout, never()).run();
    }

    @Test
    public void timeout() {
        DoubleChecker doubleChecker = new DoubleChecker(0);

        UUID uuid = UUID.randomUUID();
        Runnable mockRunnable = mock(Runnable.class);
        Runnable mockRunnableTimeout = mock(Runnable.class);

        doubleChecker.init(uuid, mockRunnable, mockRunnableTimeout);
        doubleChecker.close();

        verify(mockRunnable, never()).run();
        verify(mockRunnableTimeout).run();
    }

    @Test
    public void reset() {
        DoubleChecker doubleChecker = new DoubleChecker();

        UUID uuid = UUID.randomUUID();

        doubleChecker.init(uuid, () -> {
        }, () -> {
        });
        doubleChecker.reset(uuid);

        Map taskMap = (Map) Whitebox.getInternalState(doubleChecker, "nextTasks");
        assertFalse(taskMap.containsKey(uuid));
    }
}