package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import io.github.wysohn.rapidframework3.core.caching.CachedElement;
import io.github.wysohn.rapidframework3.core.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework3.core.main.Manager;
import io.github.wysohn.rapidframework3.core.serialize.GsonSerializer;

public class ElementCachingManagerModule<K, V extends CachedElement<K>> extends AbstractModule {
    private final Class<? extends Manager> clazz;
    private final TypeLiteral<ISerializer<V>> serializerTypeLiteral;
    private final Class<V> valueClazz;

    public ElementCachingManagerModule(Class<? extends Manager> clazz, Class<V> valueClazz, TypeLiteral<ISerializer<V>> serializerTypeLiteral) {
        this.clazz = clazz;
        this.valueClazz = valueClazz;
        this.serializerTypeLiteral = serializerTypeLiteral;
    }

    @Override
    protected void configure() {
        MapBinder<Class<? extends Manager>, Manager> mapBinder = MapBinder.newMapBinder(binder(),
                new TypeLiteral<Class<? extends Manager>>() {
                },
                new TypeLiteral<Manager>() {
                });
        mapBinder.addBinding(clazz).to(clazz);

        bind(serializerTypeLiteral).toInstance(new GsonSerializer<>(valueClazz));
    }
}
