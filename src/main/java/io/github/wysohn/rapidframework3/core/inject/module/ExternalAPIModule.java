package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import io.github.wysohn.rapidframework3.core.api.ExternalAPI;

public class ExternalAPIModule extends AbstractModule {
    private final Pair[] pairs;

    public ExternalAPIModule(Pair... pairs) {
        this.pairs = pairs;
    }

    @Override
    protected void configure() {
        MapBinder<String, Class<? extends ExternalAPI>> mapBinder = MapBinder.newMapBinder(binder(),
                new TypeLiteral<String>() {
                },
                new TypeLiteral<Class<? extends ExternalAPI>>() {
                });

        for (Pair pair : pairs) {
            mapBinder.addBinding(pair.pluginName).toInstance(pair.apiSupport);
        }
    }

    public static class Pair {
        final String pluginName;
        final Class<? extends ExternalAPI> apiSupport;

        private Pair(String pluginName, Class<? extends ExternalAPI> apiSupport) {
            this.pluginName = pluginName;
            this.apiSupport = apiSupport;
        }

        public static Pair of(String pluginName, Class<? extends ExternalAPI> apiSupport) {
            return new Pair(pluginName, apiSupport);
        }
    }
}
