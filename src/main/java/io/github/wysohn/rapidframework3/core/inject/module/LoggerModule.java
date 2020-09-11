package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import java.util.logging.Logger;

public class LoggerModule extends AbstractModule {
    private final Logger logger;

    public LoggerModule(Logger logger) {
        this.logger = logger;
    }

    @Provides
    public Logger getLogger() {
        return logger;
    }
}
