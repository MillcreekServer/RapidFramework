package io.github.wysohn.rapidframework4.bukkit.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework4.core.inject.annotations.PluginAsyncExecutor;
import io.github.wysohn.rapidframework4.core.main.PluginMain;
import io.github.wysohn.rapidframework4.interfaces.plugin.ITaskSupervisor;
import org.bukkit.Bukkit;

import javax.inject.Singleton;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class BukkitTaskSupervisorModule extends AbstractModule {
    @Provides
    @Singleton
    ITaskSupervisor taskSupervisor(PluginMain main,
                                   @PluginAsyncExecutor ExecutorService executor) {
        return new ITaskSupervisor() {
            @Override
            public <V> Future<V> sync(Callable<V> callable) {
                return Bukkit.getScheduler().callSyncMethod(main.getPlatform(), callable);
            }

            @Override
            public void sync(Runnable runnable) {
                Bukkit.getScheduler().runTask(main.getPlatform(), runnable);
            }

            @Override
            public <V> Future<V> async(Callable<V> callable) {
                return executor.submit(callable);
            }

            @Override
            public void async(Runnable runnable) {
                async(() -> {
                    runnable.run();
                    return null;
                });
            }
        };
    }
}
