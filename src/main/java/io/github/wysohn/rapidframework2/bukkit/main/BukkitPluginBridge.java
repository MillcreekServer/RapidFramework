package io.github.wysohn.rapidframework2.bukkit.main;

import io.github.wysohn.rapidframework2.bukkit.main.objects.BukkitCommandSender;
import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.TaskSupervisor;
import org.bukkit.Bukkit;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class BukkitPluginBridge implements io.github.wysohn.rapidframework2.core.main.PluginBridge {
    private final BukkitPluginMain bukkitPluginMain;
    private final TaskSupervisor taskSupervisor = new TaskSupervisor() {
        @Override
        public <T> Future<T> runAsync(Callable<T> callable) {
            return asyncTaskExecutor.submit(callable);
        }

        @Override
        public <T> Future<T> runSync(Callable<T> callable) {
            return Bukkit.getScheduler().callSyncMethod(bukkitPluginMain, callable);
        }
    };

    public BukkitPluginBridge(BukkitPluginMain bukkitPluginMain) {
        this.bukkitPluginMain = bukkitPluginMain;
    }

    @Override
    public TaskSupervisor getTaskSupervisor() {
        return taskSupervisor;
    }

    @Override
    public void forEachSender(Consumer<ICommandSender> consumer) {
        Bukkit.getOnlinePlayers().stream()
                .map(player -> new BukkitCommandSender().setSender(player))
                .forEach(consumer);
    }

    private static final ExecutorService asyncTaskExecutor = Executors.newCachedThreadPool((runnable)->{
        Thread thread = new Thread(runnable);
        thread.setPriority(Thread.MAX_PRIORITY);
        return thread;
    });
}
