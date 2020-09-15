package io.github.wysohn.rapidframework3.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.interfaces.chat.IPlaceholderSupport;

public class MockPlaceholderModule extends AbstractModule {
    @Provides
    IPlaceholderSupport getPlaceholderSupport() {
        return (sender, str) -> "replaced!";
    }
}
