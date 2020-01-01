package io.github.wysohn.rapidframework2.core.manager.group;

import io.github.wysohn.rapidframework2.core.interfaces.entity.IPermissionHolder;

import java.util.UUID;

public interface IGroupMember extends IPermissionHolder {
    UUID getGroupUuid();
}
