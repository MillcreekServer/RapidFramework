package io.github.wysohn.rapidframework3.core.main;

import javax.inject.Inject;

public abstract class Mediator extends PluginModule {
    @Inject
    public Mediator(PluginMain main) {
        super(main);
    }
}