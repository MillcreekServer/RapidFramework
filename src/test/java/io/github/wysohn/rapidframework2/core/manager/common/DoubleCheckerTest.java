package io.github.wysohn.rapidframework2.core.manager.common;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.util.Map;
import java.util.UUID;

public class DoubleCheckerTest {
    @Test
    public void init() {
        DoubleChecker doubleChecker = new DoubleChecker();

        UUID uuid = UUID.randomUUID();

        doubleChecker.init(uuid, () -> { }, () -> { });

        Map taskMap = Whitebox.getInternalState(doubleChecker, "nextTasks");
        Assert.assertTrue(taskMap.containsKey(uuid));
    }

    @Test
    public void confirm() {
        DoubleChecker doubleChecker = new DoubleChecker();

        UUID uuid = UUID.randomUUID();
        Runnable mockRunnable = Mockito.mock(Runnable.class);
        Runnable mockRunnableTimeout = Mockito.mock(Runnable.class);

        doubleChecker.init(uuid, mockRunnable, mockRunnableTimeout);
        doubleChecker.confirm(uuid);

        Mockito.verify(mockRunnable).run();
        Mockito.verify(mockRunnableTimeout, Mockito.never()).run();
    }

    @Test
    public void timeout(){
        DoubleChecker doubleChecker = new DoubleChecker(0);

        UUID uuid = UUID.randomUUID();
        Runnable mockRunnable = Mockito.mock(Runnable.class);
        Runnable mockRunnableTimeout = Mockito.mock(Runnable.class);

        doubleChecker.init(uuid, mockRunnable, mockRunnableTimeout);
        doubleChecker.close();

        Mockito.verify(mockRunnable, Mockito.never()).run();
        Mockito.verify(mockRunnableTimeout).run();
    }

    @Test
    public void reset() {
        DoubleChecker doubleChecker = new DoubleChecker();

        UUID uuid = UUID.randomUUID();

        doubleChecker.init(uuid, () -> { }, () -> { });
        doubleChecker.reset(uuid);

        Map taskMap = Whitebox.getInternalState(doubleChecker, "nextTasks");
        Assert.assertFalse(taskMap.containsKey(uuid));
    }
}