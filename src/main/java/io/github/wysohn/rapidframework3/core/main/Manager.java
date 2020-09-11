package io.github.wysohn.rapidframework3.core.main;

import java.util.LinkedList;
import java.util.List;

public abstract class Manager extends PluginModule {
    List<Class<? extends Manager>> dependsOn = new LinkedList<>();

    public Manager(PluginMain main) {
        super(main);
    }

    protected final Manager dependsOn(Class<? extends Manager> clazz) {
        if (getClass() == clazz)
            throw new RuntimeException("A manager can't depend on itself!");

        dependsOn.add(clazz);
        return this;
    }

    public static final int NORM_PRIORITY = 5;
    public static final int SLOWEST_PRIORITY = 10;
    public static final int FASTEST_PRIORITY = 0;
}