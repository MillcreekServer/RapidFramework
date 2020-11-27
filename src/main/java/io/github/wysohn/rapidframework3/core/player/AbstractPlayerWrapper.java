package io.github.wysohn.rapidframework3.core.player;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;
import io.github.wysohn.rapidframework3.data.SimpleLocation;
import io.github.wysohn.rapidframework3.interfaces.entity.IPlayer;

import java.util.UUID;

public abstract class AbstractPlayerWrapper extends CachedElement<UUID> implements IPlayer {
    public AbstractPlayerWrapper(UUID key) {
        super(key);
    }

    public abstract boolean isOnline();

    public abstract void teleport(SimpleLocation sloc);

    public abstract void teleport(String world, double x, double y, double z);

    public abstract void teleport(String world, double x, double y, double z, float pitch, float yaw);
}
