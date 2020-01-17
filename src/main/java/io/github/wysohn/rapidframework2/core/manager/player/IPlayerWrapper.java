package io.github.wysohn.rapidframework2.core.manager.player;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.manager.NamedElement;
import io.github.wysohn.rapidframework2.core.manager.group.IGroupMember;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleChunkLocation;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleLocation;

public interface IPlayerWrapper extends ICommandSender, IGroupMember, NamedElement {
    String getDisplayName();

    SimpleLocation getLocation();

    SimpleChunkLocation getChunkLocation();

    boolean isOnline();

    void teleport(SimpleLocation sloc);

    void teleport(String world, double x, double y, double z);

    void teleport(String world, double x, double y, double z, float pitch, float yaw);
}
