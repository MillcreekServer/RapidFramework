package io.github.wysohn.rapidframework4.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DoubleChecker {
    private final ExecutorService exec = Executors.newCachedThreadPool();
    private final int AUTO_CANCEL_SECS;
    private final Map<UUID, Task> nextTasks = new HashMap<>();

    public DoubleChecker() {
        this(10);
    }

    public DoubleChecker(int AUTO_CANCEL_SECS) {
        this.AUTO_CANCEL_SECS = AUTO_CANCEL_SECS;
    }

    /**
     * Initialize the task to be executed when it is confirmed with the
     * {@link #confirm(UUID)} method. As soon as the confirm method is
     * used, the 'task' will be executed. Automatically cancels
     * after {@link DoubleChecker#AUTO_CANCEL_SECS} seconds if no subsequent action is performed.
     *
     * @param uuid      uuid associated with this task
     * @param task      the task to schedule
     * @param onTimeOut callback on timeout
     * @return true if scheduled; false if already under progress
     */
    public boolean init(UUID uuid, Runnable task, Runnable onTimeOut) {
        if (nextTasks.containsKey(uuid))
            return false;

        nextTasks.put(uuid, new Task(uuid, task, onTimeOut));
        return true;
    }

    /**
     * Execute the scheduled task.
     *
     * @param uuid uuid associated with the task
     * @return true if executed; false if nothing was scheduled.
     */
    public boolean confirm(UUID uuid) {
        Task task = nextTasks.remove(uuid);
        if (task == null)
            return false;

        task.cancelFuture();
        task.task.run();

        return true;
    }

    /**
     * Remove the scheduled task without executing the task.
     *
     * @param uuid uuid associated with the task
     * @return true if removed; false if nothing was scheduled.
     */
    public boolean reset(UUID uuid) {
        Task task = nextTasks.remove(uuid);
        if (task == null)
            return false;

        task.cancelFuture();

        return true;
    }

    /**
     * Stop all the auto cancel tasks. Blocks until scheduled tasks are done or wait 100ms.
     */
    public void close() {
        exec.shutdown();
        try {
            exec.awaitTermination(100L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {

        }
    }

    private class Task {
        private final Runnable task;
        private final Future<Void> autoCancelFuture;

        public Task(UUID uuid, Runnable task, Runnable onTimeOut) {
            this.task = task;
            this.autoCancelFuture = exec.submit(() -> {
                Thread.sleep(AUTO_CANCEL_SECS * 1000L);

                reset(uuid);
                onTimeOut.run();

                return null;
            });
        }

        public void cancelFuture() {
            autoCancelFuture.cancel(true);
        }
    }
}
