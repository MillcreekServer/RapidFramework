package io.github.wysohn.rapidframework2.core.manager.permission;

import io.github.wysohn.rapidframework2.core.interfaces.entity.IPermissionHolder;
import io.github.wysohn.rapidframework2.core.main.PluginMain;

import java.util.UUID;

public interface IParentProvider {
    IPermissionHolder getHolder(PluginMain main, UUID parentUuid);
}
