package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework3.interfaces.serialize.ITypeAsserter;

public class TypeAsserterModule extends AbstractModule {
    @Provides
    @Singleton
    ITypeAsserter getAsserter() {
        return (type) -> {
            try {
                type.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new AssertionError(type + " does not have no-args constructor, so Gson will not be " +
                        "able to properly serialize/deserialize it.");
            }
        };
    }
}
