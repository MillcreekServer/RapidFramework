package io.github.wysohn.rapidframework3.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.interfaces.store.IKeyValueStorage;

public class MockKeyValueStorageModule extends AbstractModule {
    private final IKeyValueStorage mockStorage;

    public MockKeyValueStorageModule(IKeyValueStorage mockStorage) {
        this.mockStorage = mockStorage;
    }

    @Provides
    IKeyValueStorage getStorage() {
        return mockStorage;
    }
}
