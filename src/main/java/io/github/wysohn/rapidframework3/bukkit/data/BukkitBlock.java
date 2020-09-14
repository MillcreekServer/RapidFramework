package io.github.wysohn.rapidframework3.bukkit.data;

import io.github.wysohn.rapidframework3.data.SimpleChunkLocation;
import io.github.wysohn.rapidframework3.data.SimpleLocation;
import io.github.wysohn.rapidframework3.data.Vector;
import io.github.wysohn.rapidframework3.interfaces.block.IBlock;
import org.bukkit.block.Block;

import java.util.UUID;

public class BukkitBlock implements IBlock {
    private transient final Block block;

    protected BukkitBlock(Block block) {
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
