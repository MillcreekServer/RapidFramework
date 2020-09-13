package io.github.wysohn.rapidframework3.interfaces.permissin;

import io.github.wysohn.rapidframework3.interfaces.IPluginObject;

import java.util.UUID;

public interface IPermissionHolder extends IPluginObject {
    UUID getParentUuid();
}
