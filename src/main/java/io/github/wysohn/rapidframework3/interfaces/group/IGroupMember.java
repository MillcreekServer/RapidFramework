package io.github.wysohn.rapidframework3.interfaces.group;


import io.github.wysohn.rapidframework3.interfaces.permissin.IPermissionHolder;

import java.util.UUID;

public interface IGroupMember extends IPermissionHolder {
    UUID getParentUuid();
}
