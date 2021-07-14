package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginLogger;
import io.github.wysohn.rapidframework3.interfaces.plugin.IDebugStateHandle;

import javax.inject.Singleton;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggerModule extends AbstractModule {
    private final Logger logger;

    public LoggerModule(Logger logger) {
        this.logger = logger;
    }

    @Provides
    @PluginLogger
    @Singleton
    public Logger getLogger(IDebugStateHandle debugStateHandle) {
        logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (debugStateHandle.isDebugging()) {
                    System.out.println(record.getMessage());
                }
            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        });
        return logger;
    }
}
