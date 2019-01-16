package io.github.wysohn.rapidframework.pluginbase.manager.tasks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginManager;

public class ManagerSequentialTask extends PluginManager<PluginBase> {
	private static final ExecutorService pool = Executors.newCachedThreadPool();
	
	public ManagerSequentialTask(PluginBase base, int loadPriority) {
		super(base, loadPriority);
	}

	@Override
	protected void onEnable() throws Exception {
		
	}

	@Override
	protected void onDisable() throws Exception {
		
	}

	@Override
	protected void onReload() throws Exception {
		
	}
	
	/**
	 * Everything here runs in separate thread. It's caller's responsibility to 
	 * access the server thread when necessary.
	 * @param tasks
	 * @return
	 */
	public Future<Void> schedule(Tasks tasks){
		return pool.submit(tasks);
	}
	
	private static class TaskNode{
		final String name;
		Runnable task;
		TaskNode next;
		Runnable onFail;
		TaskNode(String name, Runnable task) {
			super();
			this.name = name;
			this.task = task;
		}
	}
	
	public static class Tasks implements Callable<Void>{
		private final PluginBase base;
		private final TaskNode root;
		private Runnable onFail;
		
		private Tasks(PluginBase base, TaskNode root) {
			this.base = base;
			this.root = root;
		}

		@Override
		public Void call() throws Exception {
			TaskNode current = root;
			try {
				while(current != null) {
					base.getLogger().info(current.name+" has started.");
					pool.submit(current.task).get();
					base.getLogger().info(current.name+" is done.");
					current = current.next;
				}
			} catch (Exception e) {
				e.printStackTrace();
				if(current != null)
					current.onFail.run();
				onFail.run();
			} 

			return null;
		}
		
		public static class Builder{
			private final Tasks tasks;
			private TaskNode current;
			private Builder(PluginBase base, TaskNode root) {
				this.tasks = new Tasks(base, root);
				this.current = root;
			}
			
			public static Builder startWith(PluginBase base, String name, Runnable task) {
				return new Builder(base, new TaskNode(name, task));
			}
			
			public Builder then(String name, Runnable task) {
				current.next = new TaskNode(name, task);
				current = current.next;
				return this;
			}
			
			public Builder onFail(Runnable onFail) {
				current.onFail = onFail;
				return this;
			}
			
			public Builder onFailWhole(Runnable onFail) {
				tasks.onFail = onFail;
				return this;
			}
			
			public Tasks build() {
				return tasks;
			}
		}
	}
}
