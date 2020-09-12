package io.github.wysohn.rapidframework3.core.main;

import io.github.wysohn.rapidframework2.tools.Validation;

import javax.inject.Singleton;
import java.util.LinkedList;
import java.util.List;

public abstract class Manager extends PluginModule {
    List<Class<? extends Manager>> dependsOn = new LinkedList<>();

    /**
     * Create a Manager.
     * <p>
     * Each child class is expected to be 'injectable,' which means its constructor, field, and/or method
     * are annotated with {@link javax.inject.Inject}.
     * <p>
     * To manage the load order, you may use {@link #dependsOn(Class)} method to add another Manager
     * which has to load prior to this class.
     *
     * @param main The PluginMain
     */
    public Manager(PluginMain main) {
        super(main);

        verifySingleton(this);
    }

    private static void verifySingleton(Manager manager) {
        Validation.validate(manager,
                m -> m.getClass().getAnnotation(Singleton.class) != null,
                String.format("Manager %s is not annotated with javax.inject.Singleton!", manager.getClass()));
    }

    /**
     * Mark this Manager to depend on the provided Manager. This manager will not be loaded
     * until the specified class is loaded first.
     * <p>
     * Exception will be thrown on plugin enable phase if Managers make circular dependency.
     *
     * @param clazz class of the Manager, which this Manager depends on.
     * @return This Manager itself for builder pattern.
     */
    protected final Manager dependsOn(Class<? extends Manager> clazz) {
        if (getClass() == clazz)
            throw new RuntimeException("A manager can't depend on itself!");

        dependsOn.add(clazz);
        return this;
    }
}