package io.github.wysohn.rapidframework2.core.interfaces.entity;

import java.util.UUID;

public interface IPermissionHolder extends IPluginEntity{
    UUID getParentUuid();
}
