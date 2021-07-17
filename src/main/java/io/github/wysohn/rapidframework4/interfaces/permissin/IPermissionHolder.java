package io.github.wysohn.rapidframework4.interfaces.permissin;

import io.github.wysohn.rapidframework4.interfaces.IPluginObject;

import java.util.UUID;

public interface IPermissionHolder extends IPluginObject {
    UUID getParentUuid();
}
