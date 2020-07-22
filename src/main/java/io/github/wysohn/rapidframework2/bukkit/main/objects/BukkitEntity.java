package io.github.wysohn.rapidframework2.bukkit.main.objects;

import io.github.wysohn.rapidframework2.core.interfaces.entity.IPluginEntity;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleChunkLocation;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleLocation;
import io.github.wysohn.rapidframework2.core.objects.location.Vector;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;

import java.util.Optional;
import java.util.UUID;

public class BukkitEntity implements IPluginEntity {
    protected final transient Entity entity;

    protected BukkitEntity(Entity entity) {
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

    @Override
    public Vector getDirection() {
        return Optional.ofNullable(entity)
                .map(Entity::getFacing)
                .map(BlockFace::getDirection)
                .map(v -> new Vector(v.getX(), v.getY(), v.getZ()))
                .orElse(Vector.zero());
    }
}
