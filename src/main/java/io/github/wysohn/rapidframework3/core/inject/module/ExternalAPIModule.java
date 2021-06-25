package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import io.github.wysohn.rapidframework3.core.api.ExternalAPI;
import io.github.wysohn.rapidframework3.utils.Pair;

public class ExternalAPIModule extends AbstractModule {
    private final Pair<String, Class<? extends ExternalAPI>>[] pairs;

    @SafeVarargs
    public ExternalAPIModule(Pair<String, Class<? extends ExternalAPI>>... pairs) {
        this.pairs = pairs;
    }

    @Override
    protected void configure() {
        Multibinder<Pair<String, Class<? extends ExternalAPI>>> multibinder
                = Multibinder.newSetBinder(binder(), new TypeLiteral<Pair<String, Class<? extends ExternalAPI>>>() {});

        for (Pair<String, Class<? extends ExternalAPI>> pair : pairs) {
            multibinder.addBinding().toInstance(pair);
        }
    }
}
