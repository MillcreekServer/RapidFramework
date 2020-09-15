package io.github.wysohn.rapidframework3.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.interfaces.permissin.IParentProvider;

import static org.mockito.Mockito.mock;

public class MockParentProviderModule extends AbstractModule {
    private final IParentProvider parentProvider = mock(IParentProvider.class);

    @Provides
    public IParentProvider getParentProvider() {
        return parentProvider;
    }
}
