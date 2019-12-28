package io.github.wysohn.rapidframework2.core.manager.permission;

import io.github.wysohn.rapidframework2.core.interfaces.entity.IPermissionHolder;

import java.util.UUID;

public interface IParentProvider {
    IPermissionHolder getHolder(UUID parentUuid);
}
