package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import io.github.wysohn.rapidframework3.core.main.Manager;

public class ManagerModule extends AbstractModule {
    private final Class<? extends Manager>[] classes;

    public ManagerModule(Class<? extends Manager>... classes) {
        this.classes = classes;
    }

    @Override
    protected void configure() {
        MapBinder<Class<? extends Manager>, Manager> mapBinder = MapBinder.newMapBinder(binder(),
                new TypeLiteral<Class<? extends Manager>>() {
                },
                new TypeLiteral<Manager>() {
                });
        for (Class<? extends Manager> clazz : classes) {
            mapBinder.addBinding(clazz).to(clazz);
        }
    }
}
