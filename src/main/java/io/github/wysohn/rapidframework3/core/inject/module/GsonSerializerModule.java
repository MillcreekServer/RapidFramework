package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework3.core.serialize.GsonSerializer;
import io.github.wysohn.rapidframework3.interfaces.serialize.CustomAdapter;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework3.interfaces.serialize.ITypeAsserter;
import io.github.wysohn.rapidframework3.utils.Pair;

public class GsonSerializerModule extends AbstractModule {
    private final Pair<Class<?>, CustomAdapter<?>>[] adapters;

    @SafeVarargs
    public GsonSerializerModule(Pair<Class<?>, CustomAdapter<?>>... adapters) {
        this.adapters = adapters;
    }


    @Provides
    @Singleton
    ISerializer getSerializer(ITypeAsserter asserter) {
        for (Pair<Class<?>, CustomAdapter<?>> adapter : adapters) {
            asserter.assertClass(adapter.key);
        }

        return new GsonSerializer(adapters);
    }
}
