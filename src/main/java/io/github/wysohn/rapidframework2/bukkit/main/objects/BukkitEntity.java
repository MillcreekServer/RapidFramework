package io.github.wysohn.rapidframework2.bukkit.main.objects;

import io.github.wysohn.rapidframework2.core.interfaces.ILocatable;
import io.github.wysohn.rapidframework2.core.interfaces.IPluginObject;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleChunkLocation;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleLocation;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class BukkitEntity implements IPluginObject, ILocatable {
    protected final transient Entity entity;

    public BukkitEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public UUID getUuid() {
        return entity.getUniqueId();
    }

    @Override
    public SimpleLocation getSloc() {
        return new SimpleLocation(entity.getWorld().getName(),
                entity.getLocation().getX(),
                entity.getLocation().getY(),
                entity.getLocation().getZ());
    }

    @Override
    public SimpleChunkLocation getScloc() {
        return new SimpleChunkLocation(entity.getWorld().getName(),
                entity.getLocation().getX(),
                entity.getLocation().getY(),
                entity.getLocation().getZ());
    }
}
