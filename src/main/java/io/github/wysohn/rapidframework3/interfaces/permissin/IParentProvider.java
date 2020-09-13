package io.github.wysohn.rapidframework3.interfaces.permissin;

import io.github.wysohn.rapidframework3.core.main.PluginMain;

import java.util.UUID;

public interface IParentProvider {
    IPermissionHolder getHolder(PluginMain main, UUID parentUuid);
}
