package io.github.wysohn.rapidframework3.interfaces.permissin;

import java.util.UUID;

public interface IParentProvider {
    IPermissionHolder getHolder(UUID parentUuid);
}
