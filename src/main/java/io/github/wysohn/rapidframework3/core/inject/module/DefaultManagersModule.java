package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import io.github.wysohn.rapidframework3.core.language.ManagerLanguage;
import io.github.wysohn.rapidframework3.core.main.Manager;
import io.github.wysohn.rapidframework3.core.main.ManagerConfig;

public class DefaultManagersModule extends AbstractModule {
    private final Class<? extends Manager>[] classes;

    public DefaultManagersModule() {
        this.classes = new Class[]{
                ManagerLanguage.class,
                ManagerConfig.class,
        };
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
