package io.github.wysohn.rapidframework4.bukkit.testutils.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

/**
 * These are codes copied directly from spigotmc for test purpose.
 */
public class CraftInventory implements Inventory {
    public ItemStack[] inventory = new ItemStack[6 * 9];

    public int getSize() {
        return inventory.length;
    }

    public String getName() {
        return "TestInventory";
    }

    public ItemStack getItem(int index) {
        ItemStack item = inventory[index];
        return item == null ? null : new ItemStack(item);
    }

    public ItemStack[] getContents() {
        ItemStack[] items = new ItemStack[getSize()];
        ItemStack[] mcItems = inventory;

        for (int i = 0; i < mcItems.length; i++) {
            items[i] = mcItems[i] == null ? null : new ItemStack(mcItems[i]);
        }

        return items;
    }

    public void setContents(ItemStack[] items) {
        if (inventory.length < items.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + inventory.length + " or less");
        }

        ItemStack[] mcItems = inventory;

        for (int i = 0; i < mcItems.length; i++) {
            if (i >= items.length) {
                mcItems[i] = null;
            } else {
                mcItems[i] = new ItemStack(items[i]);
            }
        }
    }

    @NotNull
    @Override
    public ItemStack[] getStorageContents() {
        return inventory;
    }

    @Override
    public void setStorageContents(@NotNull ItemStack[] items) throws IllegalArgumentException {
        inventory = items;
    }

    public void setItem(int index, ItemStack item) {
        inventory[index] = ((item == null || item.getType() == Material.AIR) ? null : new ItemStack(item));
    }

    public boolean contains(Material material) {
        for (ItemStack item : inventory) {
            if (item != null && item.getType() == material)
                return true;
        }

        return false;
    }

    public boolean contains(ItemStack item) {
        if (item == null) {
            return false;
        }
        for (ItemStack i : getContents()) {
            if (item.equals(i)) {
                return true;
            }
        }
        return false;
    }


    public boolean contains(Material material, int amount) {
        int amt = 0;
        for (ItemStack item : getContents()) {
            if (item != null && item.getType() == material) {
                amt += item.getAmount();
            }
        }
        return amt >= amount;
    }

    public boolean contains(ItemStack item, int amount) {
        if (item == null) {
            return false;
        }
        int amt = 0;
        for (ItemStack i : getContents()) {
            if (item.equals(i)) {
                amt += item.getAmount();
            }
        }
        return amt >= amount;
    }

    @Override
    public boolean containsAtLeast(@Nullable ItemStack item, int amount) {
        if (item == null) {
            return false;
        }
        if (amount <= 0) {
            return true;
        }
        for (ItemStack i : getStorageContents()) {
            if (item.isSimilar(i) && (amount -= i.getAmount()) <= 0) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    public HashMap<Integer, ItemStack> all(Material material) {
        HashMap<Integer, ItemStack> slots = new HashMap<Integer, ItemStack>();

        ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            if (item != null && item.getType() == material) {
                slots.put(i, item);
            }
        }
        return slots;
    }

    @NotNull
    public HashMap<Integer, ItemStack> all(ItemStack item) {
        HashMap<Integer, ItemStack> slots = new HashMap<Integer, ItemStack>();
        if (item != null) {
            ItemStack[] inventory = getContents();
            for (int i = 0; i < inventory.length; i++) {
                if (item.equals(inventory[i])) {
                    slots.put(i, inventory[i]);
                }
            }
        }
        return slots;
    }


    public int first(Material material) {
        ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            if (item != null && item.getType() == material) {
                return i;
            }
        }
        return -1;
    }

    public int first(ItemStack item) {
        return first(item, true);
    }

    public int first(ItemStack item, boolean withAmount) {
        if (item == null) {
            return -1;
        }
        ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) continue;

            boolean equals = false;

            if (withAmount) {
                equals = item.equals(inventory[i]);
            } else {
                equals = item.getType() == inventory[i].getType() && item.getDurability() == inventory[i].getDurability() && item.getEnchantments().equals(inventory[i].getEnchantments());
            }

            if (equals) {
                return i;
            }
        }
        return -1;
    }

    public int firstEmpty() {
        ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public int firstPartial(Material material) {
        ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            if (item != null && item.getType() == material && item.getAmount() < item.getMaxStackSize()) {
                return i;
            }
        }
        return -1;
    }

    public int firstPartial(ItemStack item) {
        ItemStack[] inventory = getContents();
        ItemStack filteredItem = new ItemStack(item);
        if (item == null) {
            return -1;
        }
        for (int i = 0; i < inventory.length; i++) {
            ItemStack cItem = inventory[i];
            if (cItem != null && cItem.getType() == filteredItem.getType() && cItem.getAmount() < cItem.getMaxStackSize() && cItem.getDurability() == filteredItem.getDurability() && cItem.getEnchantments().equals(filteredItem.getEnchantments())) {
                return i;
            }
        }
        return -1;
    }

    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        HashMap<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

        /* TODO: some optimization
         *  - Create a 'firstPartial' with a 'fromIndex'
         *  - Record the lastPartial per Material
         *  - Cache firstEmpty result
         */

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            while (true) {
                // Do we already have a stack of it?
                int firstPartial = firstPartial(item);

                // Drat! no partial stack
                if (firstPartial == -1) {
                    // Find a free spot!
                    int firstFree = firstEmpty();

                    if (firstFree == -1) {
                        // No space at all!
                        leftover.put(i, item);
                        break;
                    } else {
                        // More than a single stack!
                        if (item.getAmount() > getMaxItemStack()) {
                            ItemStack stack = new ItemStack(item.getType(), getMaxItemStack(), item.getDurability());
                            stack.addUnsafeEnchantments(item.getEnchantments());
                            setItem(firstFree, stack);
                            item.setAmount(item.getAmount() - getMaxItemStack());
                        } else {
                            // Just store it
                            setItem(firstFree, item);
                            break;
                        }
                    }
                } else {
                    // So, apparently it might only partially fit, well lets do just that
                    ItemStack partialItem = getItem(firstPartial);

                    int amount = item.getAmount();
                    int partialAmount = partialItem.getAmount();
                    int maxAmount = partialItem.getMaxStackSize();

                    // Check if it fully fits
                    if (amount + partialAmount <= maxAmount) {
                        partialItem.setAmount(amount + partialAmount);
                        break;
                    }

                    // It fits partially
                    partialItem.setAmount(maxAmount);
                    item.setAmount(amount + partialAmount - maxAmount);
                }
            }
        }
        return leftover;
    }

    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        HashMap<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

        // TODO: optimization

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            int toDelete = item.getAmount();

            while (true) {
                int first = first(item, false);

                // Drat! we don't have this type in the inventory
                if (first == -1) {
                    item.setAmount(toDelete);
                    leftover.put(i, item);
                    break;
                } else {
                    ItemStack itemStack = getItem(first);
                    int amount = itemStack.getAmount();

                    if (amount <= toDelete) {
                        toDelete -= amount;
                        // clear the slot, all used up
                        clear(first);
                    } else {
                        // split the stack and store
                        itemStack.setAmount(amount - toDelete);
                        setItem(first, itemStack);
                        toDelete = 0;
                    }
                }

                // Bail when done
                if (toDelete <= 0) {
                    break;
                }
            }
        }
        return leftover;
    }

    private int getMaxItemStack() {
        return 64;
    }

    public void remove(Material material) {
        ItemStack[] items = getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getType() == material) {
                clear(i);
            }
        }
    }

    public void remove(ItemStack item) {
        ItemStack[] items = getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].equals(item)) {
                clear(i);
            }
        }
    }

    public void clear(int index) {
        setItem(index, null);
    }

    public void clear() {
        for (int i = 0; i < getSize(); i++) {
            clear(i);
        }
    }

    public ListIterator<ItemStack> iterator() {
        return new InventoryIterator();
    }

    public ListIterator<ItemStack> iterator(int index) {
        return new InventoryIterator();
    }

    @Nullable
    @Override
    public Location getLocation() {
        return null;
    }

    public List<HumanEntity> getViewers() {
        return Collections.emptyList();
    }

    public String getTitle() {
        return "InventoryTest";
    }

    public InventoryType getType() {
        return InventoryType.CHEST;
    }

    public InventoryHolder getHolder() {
        return null;
    }

    public int getMaxStackSize() {
        return 6 * 9;
    }

    public void setMaxStackSize(int size) {

    }

    private class InventoryIterator implements ListIterator<ItemStack> {
        int index = -1;

        @Override
        public boolean hasNext() {
            return index < inventory.length;
        }

        @Override
        public ItemStack next() {
            return inventory[++index];
        }

        @Override
        public boolean hasPrevious() {
            return inventory.length > 0 && index > 0;
        }

        @Override
        public ItemStack previous() {
            return inventory[--index];
        }

        @Override
        public int nextIndex() {
            return index + 1;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            if (index >= 0 && index < inventory.length)
                inventory[index] = null;
        }

        @Override
        public void set(ItemStack itemStack) {
            if (index >= 0 && index < inventory.length)
                inventory[index] = itemStack;
        }

        @Override
        public void add(ItemStack itemStack) {
            //?
        }
    }
}