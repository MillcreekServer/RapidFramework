package io.github.wysohn.rapidframework3.testmodules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.core.api.ManagerExternalAPI;
import io.github.wysohn.rapidframework3.core.main.ManagerConfig;
import io.github.wysohn.rapidframework3.core.main.PluginMain;

import java.util.Optional;
import java.util.logging.Logger;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockMainModule extends AbstractModule {
    public final PluginMain mockMain;
    public final Logger mockLogger;
    public final ManagerConfig mockConfig;
    public final ManagerExternalAPI mockApiManager;

    public MockMainModule() {
        this.mockMain = mock(PluginMain.class);
        this.mockLogger = mock(Logger.class);
        this.mockConfig = mock(ManagerConfig.class);
        this.mockApiManager = mock(ManagerExternalAPI.class);

        when(mockMain.getLogger()).thenReturn(mockLogger);
        when(mockMain.conf()).thenReturn(mockConfig);
        when(mockMain.api()).thenReturn(mockApiManager);
        when(mockConfig.get(eq("dbType"))).thenReturn(Optional.of("file"));
    }

    @Provides
    PluginMain getMain() {
        return mockMain;
    }
}
