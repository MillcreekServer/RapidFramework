package io.github.wysohn.rapidframework2.main;

import io.github.wysohn.rapidframework2.manager.Manager;
import io.github.wysohn.rapidframework2.manager.command.ManagerCommand;

import java.util.HashMap;
import java.util.Map;

public class PluginMain {
    private final ManagerCommand managerCommand;

    private final Map<Class<? extends Manager>, Manager> managers = new HashMap<>();
    private final Map<String, Manager> managersStr = new HashMap<>();

    private final String adminPermission;

    public PluginMain(String mainCommand, String adminPermission) {
        this.managerCommand = new ManagerCommand(mainCommand, adminPermission);
        this.adminPermission = adminPermission;
    }

    public void registerManager(Manager manager) {
        managers.put(manager.getClass(), manager);
        managersStr.put(manager.getClass().getSimpleName(), manager);
    }
}
