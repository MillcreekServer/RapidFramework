package io.github.wysohn.rapidframework4.bukkit.manager.task;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework4.core.inject.annotations.PluginLogger;
import io.github.wysohn.rapidframework4.core.main.Manager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Simple class designed to do some repetitive tasks 'lazily.' This 'lazy'
 * behavior is useful when several repetitive tasks that doesn't have to run
 * every time. For example, to keep the data in memory safe, we write them into
 * the permanent storage (like HDD), however, the data in memory often change
 * really quickly, and writing the data to permanent storage every single time
 * when the data in memory gets changed is quite a waste of resources. If there
 * was 100 changes in the data, it's better to wait for certain amount of time
 * before actually writing data to the storage to get up to date memory data
 * instead of saving them 100 times individually.
 *
 * @author wysohn
 */
@Singleton
public class ManagerLazyTask extends Manager {
    private final ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);

    private final Map<String, ScheduledFuture<?>> tasks = new HashMap<>();
    private final Logger logger;

    @Inject
    public ManagerLazyTask(@PluginLogger Logger logger) {
        this.logger = logger;
    }

    @Override
    public void enable() throws Exception {

    }

    @Override
    public void load() throws Exception {

    }

    @Override
    public void disable() throws Exception {
        logger.info("Waiting for lazy tasks to finish...");
        pool.shutdown();
        logger.info("Done.");
    }

    /**
     * schedule the lazy task. Whether the task run successfully or fails and throws exception,
     * it will remove the tasks from the queue once executed.
     *
     * @param key   the unique key to distinguish each task
     * @param run   task to run. This task will run in separate thread.
     * @param delay the delay in milliseconds to execute the task
     * @return true if scheduled; false if already scheduled
     */
    public boolean scheduleTask(String key, Runnable run, long delay) {
        synchronized (tasks) {
            if (tasks.containsKey(key))
                return false;

            tasks.put(key, pool.schedule(() -> {
                try {
                    run.run();
                } finally {
                    synchronized (tasks) {
                        tasks.remove(key);
                    }
                }
            }, delay, TimeUnit.MILLISECONDS));

            return true;
        }
    }
}
