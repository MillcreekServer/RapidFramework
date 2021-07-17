package io.github.wysohn.rapidframework4.bukkit.data;

import io.github.wysohn.rapidframework4.data.SimpleChunkLocation;
import io.github.wysohn.rapidframework4.data.SimpleLocation;
import io.github.wysohn.rapidframework4.data.Vector;
import io.github.wysohn.rapidframework4.interfaces.entity.IPluginEntity;
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
