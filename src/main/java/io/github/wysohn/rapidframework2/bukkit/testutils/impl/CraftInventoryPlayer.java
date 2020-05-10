package io.github.wysohn.rapidframework2.bukkit.testutils.impl;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftInventoryPlayer extends CraftInventory implements PlayerInventory {
    public ItemStack[] extraContents;
    public ItemStack[] armorContents;
    public int heldItemSlot = 0;
    public ItemStack itemInHand;
    public ItemStack itemInOffHand;
    public HumanEntity owner;

    @Override
    public int getSize() {
        return super.getSize() - 4;
    }

    public ItemStack getItemInHand() {
        return new ItemStack(itemInHand);
    }

    public void setItemInHand(ItemStack stack) {
        setItem(getHeldItemSlot(), stack);
    }

    public int getHeldItemSlot() {
        return heldItemSlot;
    }

    @Override
    public void setHeldItemSlot(int slot) {
        heldItemSlot = slot;
    }

    public ItemStack getHelmet() {
        return getItem(getSize() + 3);
    }

    public void setHelmet(ItemStack helmet) {
        setItem(getSize() + 3, helmet);
    }

    public ItemStack getChestplate() {
        return getItem(getSize() + 2);
    }

    public void setChestplate(ItemStack chestplate) {
        setItem(getSize() + 2, chestplate);
    }

    public ItemStack getLeggings() {
        return getItem(getSize() + 1);
    }

    public void setLeggings(ItemStack leggings) {
        setItem(getSize() + 1, leggings);
    }

    public ItemStack getBoots() {
        return getItem(getSize() + 0);
    }

    public void setBoots(ItemStack boots) {
        setItem(getSize() + 0, boots);
    }

    @Override
    public void setItem(@NotNull EquipmentSlot slot, @Nullable ItemStack item) {
        inventory[getSize() + slot.ordinal()] = item;
    }

    @NotNull
    @Override
    public ItemStack getItem(@NotNull EquipmentSlot slot) {
        return inventory[getSize() + slot.ordinal()];
    }

    @NotNull
    @Override
    public ItemStack getItemInMainHand() {
        return itemInHand;
    }

    @Override
    public void setItemInMainHand(@Nullable ItemStack item) {
        itemInHand = item;
    }

    @NotNull
    @Override
    public ItemStack getItemInOffHand() {
        return itemInOffHand;
    }

    @Override
    public void setItemInOffHand(@Nullable ItemStack item) {
        itemInOffHand = item;
    }

    public ItemStack[] getArmorContents() {
        ItemStack[] mcItems = armorContents;
        ItemStack[] ret = new ItemStack[mcItems.length];

        for (int i = 0; i < mcItems.length; i++) {
            ret[i] = new ItemStack(mcItems[i]);
        }
        return ret;
    }

    public void setArmorContents(ItemStack[] items) {
        int cnt = getSize();

        if (items == null) {
            items = new ItemStack[4];
        }
        for (ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR) {
                clear(cnt++);
            } else {
                setItem(cnt++, item);
            }
        }
    }

    @NotNull
    @Override
    public ItemStack[] getExtraContents() {
        return new ItemStack[0];
    }

    @Override
    public void setExtraContents(@Nullable ItemStack[] items) {
        extraContents = items;
    }

    @Override
    public HumanEntity getHolder() {
        return owner;
    }
}