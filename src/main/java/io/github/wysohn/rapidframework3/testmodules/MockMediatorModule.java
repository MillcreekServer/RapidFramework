package io.github.wysohn.rapidframework3.testmodules;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import io.github.wysohn.rapidframework3.core.main.Mediator;

import java.util.HashMap;
import java.util.Map;

public class MockMediatorModule extends AbstractModule {
    private final Map<Class<? extends Mediator>, Mediator> mediatorMap = new HashMap<>();

    public MockMediatorModule(Mediator... mediators) {
        for (Mediator mediator : mediators) {
            mediatorMap.put(mediator.getClass(), mediator);
        }
    }

    @Override
    protected void configure() {
        MapBinder<Class<? extends Mediator>, Mediator> mapBinder = MapBinder.newMapBinder(binder(),
                new TypeLiteral<Class<? extends Mediator>>() {
                },
                new TypeLiteral<Mediator>() {
                });
        mediatorMap.forEach((clazz, mediator) -> mapBinder.addBinding(clazz).toInstance(mediator));
    }
}
