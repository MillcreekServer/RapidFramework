package io.github.wysohn.rapidframework3.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.core.main.PluginMain;

import java.util.logging.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockMainModule extends AbstractModule {
    private final PluginMain mockMain;
    private final Logger mockLogger;

    public MockMainModule() {
        this.mockMain = mock(PluginMain.class);
        this.mockLogger = mock(Logger.class);

        when(mockMain.getLogger()).thenReturn(mockLogger);
    }

    public PluginMain getMockMain() {
        return mockMain;
    }

    public Logger getMockLogger() {
        return mockLogger;
    }

    @Provides
    PluginMain getMain() {
        return mockMain;
    }
}
