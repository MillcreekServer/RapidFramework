package io.github.wysohn.rapidframework2.core.manager.player;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.manager.NamedElement;
import io.github.wysohn.rapidframework2.core.manager.group.IGroupMember;

public interface IPlayerWrapper extends ICommandSender, IGroupMember, NamedElement {
    String getDisplayName();
}
