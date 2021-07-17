package io.github.wysohn.rapidframework4.testmodules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework4.interfaces.plugin.IDebugStateHandle;

public class MockDebugStateHandleModule extends AbstractModule {
    @Provides
    @Singleton
    IDebugStateHandle debugStateHandle() {
        return new IDebugStateHandle() {
            @Override
            public boolean isDebugging() {
                return false;
            }

            @Override
            public void setDebugging(boolean state) {

            }
        };
    }
}
