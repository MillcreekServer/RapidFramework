package io.github.wysohn.rapidframework4.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework4.core.inject.annotations.PluginAsyncExecutor;

import javax.inject.Named;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceModule extends AbstractModule {
    @Provides
    @Singleton
    @PluginAsyncExecutor
    ExecutorService executorService(@Named("pluginName") String pluginName) {
        return Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("AsyncTask - " + pluginName);
            thread.setPriority(Thread.NORM_PRIORITY - 1);
            return thread;
        });
    }
}
