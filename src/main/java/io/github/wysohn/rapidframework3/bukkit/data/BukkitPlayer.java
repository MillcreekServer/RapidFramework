package io.github.wysohn.rapidframework3.bukkit.data;

import io.github.wysohn.rapidframework3.bukkit.utils.InventoryUtil;
import io.github.wysohn.rapidframework3.core.player.AbstractPlayerWrapper;
import io.github.wysohn.rapidframework3.data.SimpleChunkLocation;
import io.github.wysohn.rapidframework3.data.SimpleLocation;
import io.github.wysohn.rapidframework3.data.Vector;
import io.github.wysohn.rapidframework3.interfaces.IMemento;
import io.github.wysohn.rapidframework3.interfaces.entity.IPlayer;
import io.github.wysohn.rapidframework3.utils.Validation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class BukkitPlayer extends AbstractPlayerWrapper implements IPlayer {
    protected transient Player sender;

    private String lastKnownName;

    private BukkitPlayer() {
        super(null);
    }

    private BukkitPlayer(BukkitPlayer copy){
        super(copy.getKey());
        sender = copy.sender;
        lastKnownName = copy.lastKnownName;
    }

    protected BukkitPlayer(UUID key) {
        super(key);
    }

    @Override
    public UUID getUuid() {
        return getKey();
    }

    private transient boolean legacy = false;
    private transient Method handleMethod = null;
    private transient Field localeField = null;

    @Nullable
    private String getLocaleReflection(Player sender) {
        try {
            if (handleMethod == null)
                handleMethod = sender.getClass().getDeclaredMethod("getHandle");

            Object handle = handleMethod.invoke(sender);
            if (localeField == null)
                localeField = handle.getClass().getDeclaredField("locale");

            return (String) localeField.get(handle);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String attemptLegacyLocale(Player sender) {
        if (legacy)
            return getLocaleReflection(sender);

        try {
            return sender.getLocale();
        } catch (NoSuchMethodError ex) {
            legacy = true;
            return getLocaleReflection(sender);
        }
    }


    @Override
    public Locale getLocale() {
        return Optional.ofNullable(sender)
                .map(this::attemptLegacyLocale)
                .map(locale -> locale.replace('_', '-'))
                .map(locale -> locale.split("-")[0])
                .map(Locale::forLanguageTag)
                .orElse(Locale.ENGLISH);
    }

    @Override
    public void sendMessageRaw(boolean conversation, String... msg) {
        if (conversation) {
            for (String s : msg) {
                sender.sendRawMessage(s);
            }
        } else {
            sender.sendMessage(msg);
        }
    }

    @Override
    public boolean hasPermission(String... permissions) {
        return Arrays.stream(permissions).anyMatch(sender::hasPermission);
    }

    @Override
    public String getDisplayName() {
        return Optional.ofNullable(lastKnownName)
                .filter(name -> name.trim().length() > 0)
                .orElseGet(() -> Optional.ofNullable(sender)
                        .map(Player::getDisplayName)
                        .orElse("<Unknown>"));
    }

    @Override
    public boolean isConversing() {
        return Optional.ofNullable(sender)
                .map(Conversable::isConversing)
                .orElse(false);
    }

    public BukkitPlayer setSender(Player sender) {
        this.sender = sender;
        return this;
    }

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;
        setStringKey(lastKnownName);
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
            // max stack can be 1 (or maybe below 1 yet haven't seen such a case)
            int pack = maxStack < 2 ? 0 : amount / maxStack;
            // max stack can be 1 (or maybe below 1 yet haven't seen such a case)
            int remain = maxStack < 2 ? amount : amount % maxStack;

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
     * @return count of item that couldn't take. If all of the 'amount' are successfully taken, it will be 0.
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

            if (stackAmount < amount) { // less than target. Still may add up to the target number
                amount -= stackAmount;
                sender.getInventory().clear(slot);
            } else if (stackAmount == amount) { // exact match.
                amount -= stackAmount;
                sender.getInventory().clear(slot);
                break;
            } else { // single stack contains more than target. Adjust the amount and terminate.
                item.setAmount(stackAmount - amount);
                sender.getInventory().setItem(slot, item.clone());
                amount = 0;
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
        return Optional.ofNullable(sender)
                .map(OfflinePlayer::isOnline)
                .orElse(false);
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
                        simpleLocation.getX() + 0.5,
                        simpleLocation.getY(),
                        simpleLocation.getZ() + 0.5,
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

    @Override
    public IMemento saveState() {
        return new Memento(this);
    }

    @Override
    public void restoreState(IMemento savedState) {
        Memento mem = (Memento) savedState;

        sender.getInventory().setContents(mem.contents);
    }

    private static class Memento implements IMemento {
        final ItemStack[] contents;

        public Memento(BukkitPlayer player) {
            Validation.assertNotNull(player.sender);

            contents = contentDeepCopy(player.sender.getInventory().getContents());
        }
    }

    private static ItemStack[] contentDeepCopy(ItemStack[] original) {
        ItemStack[] copied = new ItemStack[original.length];
        for (int i = 0; i < original.length; i++) {
            if (original[i] == null)
                continue;

            copied[i] = original[i].clone();
        }
        return copied;
    }
}
