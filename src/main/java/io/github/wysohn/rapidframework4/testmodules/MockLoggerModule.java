package io.github.wysohn.rapidframework4.testmodules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework4.core.inject.annotations.PluginLogger;

import java.util.logging.Logger;

import static org.mockito.Mockito.mock;

public class MockLoggerModule extends AbstractModule {
    public final Logger logger = mock(Logger.class);

    @Provides
    @PluginLogger
    Logger logger() {
        return logger;
    }
}
