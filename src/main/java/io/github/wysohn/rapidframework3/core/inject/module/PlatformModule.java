package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginPlatform;

public class PlatformModule extends AbstractModule {
    private final Object platform;

    public PlatformModule(Object platform) {
        this.platform = platform;
    }

    @Provides
    @PluginPlatform
    Object getPlatform() {
        return platform;
    }
}
