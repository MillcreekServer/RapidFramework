package io.github.wysohn.rapidframework4.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import io.github.wysohn.rapidframework4.core.main.Mediator;

public class MediatorModule extends AbstractModule {
    private final Class<? extends Mediator>[] classes;

    @SafeVarargs
    public MediatorModule(Class<? extends Mediator>... classes) {
        this.classes = classes;
    }

    @Override
    protected void configure() {
        MapBinder<Class<? extends Mediator>, Mediator> mapBinder = MapBinder.newMapBinder(binder(),
                new TypeLiteral<Class<? extends Mediator>>() {
                },
                new TypeLiteral<Mediator>() {
                });
        for (Class<? extends Mediator> clazz : classes) {
            mapBinder.addBinding(clazz).to(clazz);
        }
    }
}
