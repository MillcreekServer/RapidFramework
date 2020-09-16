package io.github.wysohn.rapidframework3.testmodules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.core.inject.factory.IStorageFactory;
import io.github.wysohn.rapidframework3.interfaces.store.IKeyValueStorage;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockStorageFactoryModule extends AbstractModule {
    private final IKeyValueStorage mockStorage;

    public MockStorageFactoryModule(IKeyValueStorage mockStorage) {
        this.mockStorage = mockStorage;
    }

    public MockStorageFactoryModule() {
        this(mock(IKeyValueStorage.class));
    }

    @Provides
    IStorageFactory getFactory() {
        IStorageFactory factory = mock(IStorageFactory.class);
        when(factory.create(any(), anyString())).thenReturn(mockStorage);
        return factory;
    }
}
