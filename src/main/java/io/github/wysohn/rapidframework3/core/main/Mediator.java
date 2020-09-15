package io.github.wysohn.rapidframework3.core.main;

public abstract class Mediator extends PluginModule {
    /**
     * Create Mediator. Mediator works as a middle-man between each Manager, so it can prevent Managers
     * to be strongly coupled. It's highly recommended to perform any task through Mediator, instead
     * of using the Managers directly, to avoid coupling between classes.
     * <p>
     * Mediator loads always after every Manager classes are loaded, so you can safely assume that
     * all the registered Managers will be available when the Mediator is instantiated.
     * <p>
     * Each child class, just like Manager, is expected to be 'injectable' and 'singleton'. Refer to
     * the {@link Manager#Manager(PluginMain)} for the details
     *
     * @param main
     */
    public Mediator(PluginMain main) {
        super(main);
    }
}