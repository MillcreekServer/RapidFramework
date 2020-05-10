package io.github.wysohn.rapidframework2.bukkit.utils;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InventoryUtil {
    /**
     * Copied from CraftInventory
     * <p>
     * Same as {@link Inventory#all(ItemStack)} except it uses 'isSimilar()' instead of 'equals().'
     * In other words, get all the items yet the amount of 'item' will be ignored
     *
     * @param inventory
     * @param item
     * @return
     */
    public static HashMap<Integer, ItemStack> all(ItemStack[] inventory, ItemStack item) {
        HashMap<Integer, ItemStack> slots = new HashMap<>();
        if (item != null) {
            for (int i = 0; i < inventory.length; i++) {
                if (item.isSimilar(inventory[i])) {
                    slots.put(i, inventory[i]);
                }
            }
        }
        return slots;
    }
}
