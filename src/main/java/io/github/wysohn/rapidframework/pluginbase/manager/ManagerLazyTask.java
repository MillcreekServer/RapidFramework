package io.github.wysohn.rapidframework.pluginbase.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginManager;

/**
 * Simple class designed to do some repetitive tasks 'lazily.' This 'lazy'
 * behavior is useful when several repetitive tasks that doesn't have to run
 * every time. For example, to keep the data in memory safe, we write them into
 * the permanent storage (like HDD), however, the data in memory often change
 * really quickly, and writing the data to permanent storage every single time
 * when the data in memory gets changed is quite a waste of resources. If there
 * was 100 changes in the data, it's better to wait for certain amount of time
 * before actually writing data to the storage to get up to date memory data
 * instead of saving them 100 times individually. overheads.
 * 
 * @author wysohn
 *
 */
public class ManagerLazyTask extends PluginManager<PluginBase> {
    private final ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);

    private Map<String, ScheduledFuture<?>> tasks = new HashMap<>();

    public ManagerLazyTask(PluginBase base, int loadPriority) {
	super(base, loadPriority);
    }

    @Override
    protected void onEnable() throws Exception {

    }

    @Override
    protected void onDisable() throws Exception {
	base.getLogger().info("Waiting for lazy tasks to finish...");
	pool.shutdown();
	base.getLogger().info("Done.");
    }

    @Override
    protected void onReload() throws Exception {

    }

    /**
     * schedule the lazy task. If task fails and throws exception, it will remove
     * the tasks from the queue.
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
