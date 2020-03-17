package io.github.wysohn.rapidframework2.bukkit.main.objects;

import io.github.wysohn.rapidframework2.core.interfaces.entity.IPlayer;
import io.github.wysohn.rapidframework2.core.manager.player.AbstractPlayerWrapper;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleChunkLocation;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleLocation;
import io.github.wysohn.rapidframework2.core.objects.location.Vector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class BukkitPlayer extends AbstractPlayerWrapper implements IPlayer {
    protected transient Player sender;

    private String lastKnownName;

    private BukkitPlayer() {
        this(null);
    }

    public BukkitPlayer(UUID key) { super(key); }

    @Override
    public UUID getUuid() {
        return getKey();
    }

    @Override
    public Locale getLocale() {
        return Locale.forLanguageTag(sender.getLocale());
    }

    @Override
    public void sendMessageRaw(String... msg) {
        sender.sendMessage(msg);
    }

    @Override
    public boolean hasPermission(String... permissions) {
        return Arrays.stream(permissions).anyMatch(sender::hasPermission);
    }

    @Override
    public String getDisplayName() {
        return Optional.ofNullable(lastKnownName)
                .orElseGet(() -> Optional.ofNullable(sender)
                        .map(Player::getDisplayName)
                        .orElse("<Unknown>"));
    }

    public BukkitPlayer setSender(Player sender) {
        this.sender = sender;
        return this;
    }

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;

        notifyObservers();
    }

    public Player getSender() {
        return sender;
    }

    @Override
    public boolean isOnline() {
        return sender != null && sender.isOnline();
    }

    @Override
    public SimpleLocation getSloc() {
        return Optional.ofNullable(sender)
                .map(Player::getLocation)
                .map(location -> new SimpleLocation(location.getWorld().getName(),
                        location.getBlockX(),
                        location.getBlockY(),
                        location.getBlockZ(),
                        location.getPitch(),
                        location.getYaw()))
                .orElse(null);
    }

    @Override
    public SimpleChunkLocation getScloc() {
        return Optional.ofNullable(sender)
                .map(Player::getLocation)
                .map(location -> new SimpleChunkLocation(location.getWorld().getName(),
                        location.getBlockX() >> 4,
                        location.getBlockZ() >> 4))
                .orElse(null);
    }

    @Override
    public Vector getDirection() {
        return Optional.ofNullable(sender)
                .map(Entity::getFacing)
                .map(BlockFace::getDirection)
                .map(v -> new Vector(v.getX(), v.getY(), v.getZ()))
                .orElse(Vector.zero());
    }

    @Override
    public void teleport(SimpleLocation sloc) {
        Optional.ofNullable(sloc)
                .filter(simpleLocation -> Bukkit.getWorld(simpleLocation.getWorld()) != null)
                .map(simpleLocation -> new Location(Bukkit.getWorld(simpleLocation.getWorld()),
                        simpleLocation.getX(),
                        simpleLocation.getY(),
                        simpleLocation.getZ(),
                        simpleLocation.getYaw(),
                        simpleLocation.getPitch()))
                .ifPresent(location -> Optional.ofNullable(sender)
                        .ifPresent(p -> p.teleport(location)));
    }

    @Override
    public void teleport(String world, double x, double y, double z) {
        Optional.ofNullable(world)
                .filter(w -> Bukkit.getWorld(w) != null)
                .map(w -> new Location(Bukkit.getWorld(w),
                        x,
                        y,
                        z))
                .ifPresent(location -> Optional.ofNullable(sender)
                        .ifPresent(p -> p.teleport(location)));
    }

    @Override
    public void teleport(String world, double x, double y, double z, float pitch, float yaw) {
        Optional.ofNullable(world)
                .filter(w -> Bukkit.getWorld(w) != null)
                .map(w -> new Location(Bukkit.getWorld(w),
                        x,
                        y,
                        z,
                        yaw,
                        pitch))
                .ifPresent(location -> Optional.ofNullable(sender)
                        .ifPresent(p -> p.teleport(location)));
    }
}
