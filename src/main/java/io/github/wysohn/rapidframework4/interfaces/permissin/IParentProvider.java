package io.github.wysohn.rapidframework4.interfaces.permissin;

import java.util.UUID;

public interface IParentProvider {
    IPermissionHolder getHolder(UUID parentUuid);
}
