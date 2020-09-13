package io.github.wysohn.rapidframework3.core.player;

import io.github.wysohn.rapidframework2.core.objects.location.SimpleLocation;
import io.github.wysohn.rapidframework3.core.caching.CachedElement;
import io.github.wysohn.rapidframework3.interfaces.entity.IPlayer;

import java.util.UUID;

public abstract class AbstractPlayerWrapper extends CachedElement<UUID> implements IPlayer {
    public AbstractPlayerWrapper(UUID key) {
        super(key);
    }

    protected abstract boolean isOnline();

    protected abstract void teleport(SimpleLocation sloc);

    protected abstract void teleport(String world, double x, double y, double z);

    protected abstract void teleport(String world, double x, double y, double z, float pitch, float yaw);
}
