package io.github.wysohn.rapidframework4.testmodules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework4.interfaces.message.IBroadcaster;

public class MockBroadcasterModule extends AbstractModule {
    @Provides
    IBroadcaster broadcaster() {
        return fn -> {

        };
    }
}
