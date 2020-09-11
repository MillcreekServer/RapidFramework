package io.github.wysohn.rapidframework3.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginConfig;
import io.github.wysohn.rapidframework3.core.interfaces.serialize.IStorageSerializer;
import io.github.wysohn.rapidframework3.core.interfaces.store.temporary.IKeyValueStorage;

public class MockPluginConfigModule extends AbstractModule {
    private final IStorageSerializer mockSerializer;
    private final IKeyValueStorage mockStorage;

    public MockPluginConfigModule(IStorageSerializer mockSerializer, IKeyValueStorage mockStorage) {
        this.mockSerializer = mockSerializer;
        this.mockStorage = mockStorage;
    }

    @Provides
    @PluginConfig
    IStorageSerializer getSerializer() {
        return mockSerializer;
    }

    @Provides
    @PluginConfig
    IKeyValueStorage getStorage() {
        return mockStorage;
    }
}
