package io.github.wysohn.rapidframework2.bukkit.main.objects;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.manager.player.AbstractPlayerWrapper;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleChunkLocation;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class BukkitPlayer extends AbstractPlayerWrapper implements ICommandSender {
    protected transient Player sender;

    public BukkitPlayer(UUID key) { super(key);
    }

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
        return Optional.ofNullable(sender)
                .map(CommandSender::getName)
                .orElse("<Unknown>");
    }

    public BukkitPlayer setSender(Player sender) {
        this.sender = sender;
        return this;
    }

    public Player getSender() {
        return sender;
    }

    @Override
    public boolean isOnline() {
        return sender != null && sender.isOnline();
    }

    @Override
    public SimpleLocation getLocation() {
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
    public SimpleChunkLocation getChunkLocation() {
        return Optional.ofNullable(sender)
                .map(Player::getLocation)
                .map(location -> new SimpleChunkLocation(location.getWorld().getName(),
                        location.getBlockX() >> 4,
                        location.getBlockZ() >> 4))
                .orElse(null);
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
