package io.github.wysohn.rapidframework2.bukkit.main.objects;

import io.github.wysohn.rapidframework2.core.interfaces.block.IBlock;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleChunkLocation;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleLocation;
import io.github.wysohn.rapidframework2.core.objects.location.Vector;
import org.bukkit.block.Block;

import java.util.UUID;

public class BukkitBlock implements IBlock {
    private transient final Block block;

    public BukkitBlock(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public SimpleLocation getSloc() {
        return new SimpleLocation(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
    }

    @Override
    public SimpleChunkLocation getScloc() {
        return new SimpleChunkLocation(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
    }

    @Override
    public Vector getDirection() {
        return Vector.zero(); // block has no direction
    }

    @Override
    public UUID getUuid() {
        return null;
    }
}
