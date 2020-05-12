package io.github.wysohn.rapidframework2.bukkit.main.objects;

import io.github.wysohn.rapidframework2.bukkit.utils.InventoryUtil;
import io.github.wysohn.rapidframework2.core.interfaces.entity.IPlayer;
import io.github.wysohn.rapidframework2.core.manager.player.AbstractPlayerWrapper;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleChunkLocation;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleLocation;
import io.github.wysohn.rapidframework2.core.objects.location.Vector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

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

    public int give(Material material, int amount) {
        return give(new ItemStack(material), amount);
    }

    /**
     * @param itemStack amount set in ItemStack is ignored
     * @param amount    number of items to give. Can exceed the max stack size.
     * @return amount left. 0 if every item fit into inventory.
     */
    public int give(ItemStack itemStack, int amount) {
        if (amount < 1)
            return 0;

        int maxStack = itemStack.getType().getMaxStackSize();

        while (amount > 0) {
            int pack = amount / maxStack;
            int remain = amount % maxStack;

            int deduct = pack > 0 ? 64 : remain;

            ItemStack clone = itemStack.clone();
            clone.setAmount(deduct);
            Map<Integer, ItemStack> overflow = sender.getInventory().addItem(clone);

            int leftAmount = 0;
            for (Map.Entry<Integer, ItemStack> entry : overflow.entrySet()) {
                leftAmount += entry.getValue().getAmount();
            }

            amount -= (deduct - leftAmount);
            if (deduct == leftAmount) { // not fit into inventory at all.
                break;
            }
        }

        return amount;
    }

    public int take(Material material, int amount) {
        return take(new ItemStack(material), amount);
    }

    /**
     * @param itemStack amount set in ItemStack is ignored
     * @param amount
     * @return actual number of items took. Equals to 'amount' if there were enough items.
     */
    public int take(ItemStack itemStack, int amount) {
        if (amount < 1)
            return 0;

        itemStack = itemStack.clone();
        itemStack.setAmount(1);

        Map<Integer, ? extends ItemStack> result = InventoryUtil.all(sender.getInventory().getContents(), itemStack);
        for (Map.Entry<Integer, ? extends ItemStack> entry : result.entrySet()) {
            ItemStack item = entry.getValue();

            int slot = entry.getKey();
            int stackAmount = item.getAmount();

            if (stackAmount < amount) {
                amount -= stackAmount;
                sender.getInventory().clear(slot);
            } else if (stackAmount == amount) {
                amount -= stackAmount;
                sender.getInventory().clear(slot);
                break;
            } else {
                item.setAmount(stackAmount - amount);
                break;
            }
        }

        return amount;
    }

    public boolean contains(Material material, int amount) {
        return contains(new ItemStack(material), amount);
    }

    /**
     * This method in fact directly calls {@link org.bukkit.inventory.Inventory#containsAtLeast(ItemStack, int)}
     *
     * @param itemStack amount set in ItemStack is ignored
     * @param amount
     * @return
     */
    public boolean contains(ItemStack itemStack, int amount) {
        if (amount < 1)
            return true;

        return sender.getInventory().containsAtLeast(itemStack, amount);
    }

    @Override
    public boolean isOnline() {
        return sender != null && sender.isOnline();
    }

    @Override
    public SimpleLocation getSloc() {
        return Optional.ofNullable(sender)
                .map(Player::getLocation)
                .filter(location -> location.getWorld() != null)
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
                .filter(location -> location.getWorld() != null)
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
