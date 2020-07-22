package io.github.wysohn.rapidframework2.core.interfaces.entity;

import io.github.wysohn.rapidframework2.core.interfaces.IPluginObject;

import java.util.UUID;

public interface IPermissionHolder extends IPluginObject {
    UUID getParentUuid();
}
