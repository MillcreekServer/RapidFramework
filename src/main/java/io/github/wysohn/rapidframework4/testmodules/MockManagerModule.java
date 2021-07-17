package io.github.wysohn.rapidframework4.testmodules;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import io.github.wysohn.rapidframework4.core.main.Manager;

import java.util.HashMap;
import java.util.Map;

public class MockManagerModule extends AbstractModule {
    private final Map<Class<? extends Manager>, Manager> managerMap = new HashMap<>();

    public MockManagerModule(Manager... managers) {
        for (Manager manager : managers) {
            managerMap.put(manager.getClass(), manager);
        }
    }

    @Override
    protected void configure() {
        MapBinder<Class<? extends Manager>, Manager> mapBinder = MapBinder.newMapBinder(binder(),
                new TypeLiteral<Class<? extends Manager>>() {
                },
                new TypeLiteral<Manager>() {
                });
        managerMap.forEach((clazz, manager) -> mapBinder.addBinding(clazz).toInstance(manager));
    }
}
