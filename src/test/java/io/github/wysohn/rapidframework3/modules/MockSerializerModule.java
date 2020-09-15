package io.github.wysohn.rapidframework3.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;

public class MockSerializerModule extends AbstractModule {
    private final ISerializer serializer;

    public MockSerializerModule(ISerializer serializer) {
        this.serializer = serializer;
    }

    @Provides
    ISerializer serializer() {
        return serializer;
    }
}
