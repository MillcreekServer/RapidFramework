package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import io.github.wysohn.rapidframework3.core.caching.CachedElement;
import io.github.wysohn.rapidframework3.core.main.Manager;

public class ElementCachingManagerModule<K, V extends CachedElement<K>> extends AbstractModule {
    private final Class<? extends Manager> clazz;

    public ElementCachingManagerModule(Class<? extends Manager> clazz,
                                       Class<V> valueClazz) {
        this.clazz = clazz;
        assertType(valueClazz);
    }

    @Override
    protected void configure() {
        MapBinder<Class<? extends Manager>, Manager> mapBinder = MapBinder.newMapBinder(binder(),
                new TypeLiteral<Class<? extends Manager>>() {
                },
                new TypeLiteral<Manager>() {
                });
        mapBinder.addBinding(clazz).to(clazz);
    }

    private static <V> void assertType(Class<V> clazz) {
        try {
            clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new AssertionError(clazz + " does not have no-args constructor, so Gson will not be " +
                    "able to properly serialize/deserialize it.");
        }
    }
}
