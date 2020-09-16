package io.github.wysohn.rapidframework3.testmodules;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import io.github.wysohn.rapidframework3.core.api.ExternalAPI;
import io.github.wysohn.rapidframework3.utils.Pair;

public class MockExternalAPIModule extends AbstractModule {
    private final Pair<String, ExternalAPI>[] pairs;

    @SafeVarargs
    public MockExternalAPIModule(Pair<String, ExternalAPI>... pairs) {
        this.pairs = pairs;
    }

    @Override
    protected void configure() {
        MapBinder<String, Class<? extends ExternalAPI>> mapBinder = MapBinder.newMapBinder(binder(),
                new TypeLiteral<String>() {
                },
                new TypeLiteral<Class<? extends ExternalAPI>>() {
                });

        for (Pair<String, ExternalAPI> pair : pairs) {
            mapBinder.addBinding(pair.key).toInstance(pair.value.getClass());
        }
    }
}
