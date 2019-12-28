package io.github.wysohn.rapidframework2.core.interfaces.entity;

import io.github.wysohn.rapidframework2.core.manager.group.IGroupMember;

public interface IPlayerWrapper extends ICommandSender, IGroupMember {
    public String getDisplayName();
}
