package io.github.wysohn.rapidframework3.testmodules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework3.interfaces.plugin.IShutdownHandle;

public class MockShutdownModule extends AbstractModule {
    private final IShutdownHandle shutdownHandle;

    public MockShutdownModule(IShutdownHandle shutdownHandle) {
        this.shutdownHandle = shutdownHandle;
    }

    @Provides
    @Singleton
    IShutdownHandle shutdownHandle() {
        return shutdownHandle;
    }
}
