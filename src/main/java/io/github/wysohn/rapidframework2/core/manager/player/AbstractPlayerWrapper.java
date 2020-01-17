package io.github.wysohn.rapidframework2.core.manager.player;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.manager.caching.CachedElement;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleChunkLocation;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleLocation;

import java.util.UUID;

public abstract class AbstractPlayerWrapper extends CachedElement<UUID> implements ICommandSender {
    public AbstractPlayerWrapper(UUID key) {
        super(key);
    }

    protected abstract boolean isOnline();

    protected abstract SimpleLocation getLocation();

    protected abstract SimpleChunkLocation getChunkLocation();

    protected abstract void teleport(SimpleLocation sloc);

    protected abstract void teleport(String world, double x, double y, double z);

    protected abstract void teleport(String world, double x, double y, double z, float pitch, float yaw);
}
