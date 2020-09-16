package io.github.wysohn.rapidframework3.testmodules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.core.main.PluginMain;

import static org.mockito.Mockito.mock;

public class MockMainModule extends AbstractModule {
    public final PluginMain mockMain;

    public MockMainModule() {
        this.mockMain = mock(PluginMain.class);
    }

    @Provides
    PluginMain getMain() {
        return mockMain;
    }
}
