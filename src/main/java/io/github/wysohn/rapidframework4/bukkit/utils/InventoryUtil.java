package io.github.wysohn.rapidframework4.bukkit.utils;

import io.github.wysohn.rapidframework4.core.language.ManagerLanguage;
import io.github.wysohn.rapidframework4.interfaces.ICommandSender;
import io.github.wysohn.rapidframework4.interfaces.language.ILang;
import io.github.wysohn.rapidframework4.interfaces.language.ILangParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class InventoryUtil {
    private static String color(String str) {
        return Optional.ofNullable(str)
                .map(s -> ChatColor.translateAlternateColorCodes('&', str))
                .orElse(null);
    }

    private static String[] color(String... strs) {
        return Arrays.stream(strs)
                .map(InventoryUtil::color)
                .toArray(String[]::new);
    }

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

    public static void parseFirstToItemTitle(ManagerLanguage langman,
                                             ICommandSender sender,
                                             ILang lang,
                                             ILangParser parser,
                                             ItemStack itemStack) {
        String parsed = color(langman.parseFirst(sender, lang, parser));
        ItemMeta itemMeta = Optional.of(itemStack)
                .map(ItemStack::getItemMeta)
                .orElseGet(() -> Bukkit.getItemFactory().getItemMeta(itemStack.getType()));
        itemMeta.setDisplayName(parsed);

        itemStack.setItemMeta(itemMeta);
    }

    public static void parseFirstToItemTitle(ManagerLanguage langman,
                                             ICommandSender sender,
                                             ILang lang,
                                             ItemStack itemStack) {
        parseFirstToItemTitle(langman, sender, lang, ((sen, langman1) -> {
        }), itemStack);
    }

    public static void parseToItemLores(ManagerLanguage langman,
                                        ICommandSender sender,
                                        ILang lang,
                                        ILangParser parser,
                                        ItemStack itemStack,
                                        boolean cleanFirst) {
        String[] parsed = color(langman.parse(sender, lang, parser));

        ItemMeta itemMeta = Optional.of(itemStack)
                .map(ItemStack::getItemMeta)
                .orElseGet(() -> Bukkit.getItemFactory().getItemMeta(itemStack.getType()));
        List<String> lores = Optional.of(itemMeta)
                .map(ItemMeta::getLore)
                .orElseGet(ArrayList::new);
        if (cleanFirst)
            lores.clear();
        lores.addAll(Arrays.asList(parsed));
        itemMeta.setLore(lores);

        itemStack.setItemMeta(itemMeta);
    }

    public static void parseToItemLores(ManagerLanguage langman,
                                        ICommandSender sender,
                                        ILang lang,
                                        ILangParser parser,
                                        ItemStack itemStack) {
        parseToItemLores(langman, sender, lang, parser, itemStack, false);
    }

    public static void parseToItemLores(ManagerLanguage langman,
                                        ICommandSender sender,
                                        ILang lang,
                                        ItemStack itemStack,
                                        boolean cleanFirst) {
        parseToItemLores(langman, sender, lang, ((sen, langman1) -> {
        }), itemStack, cleanFirst);
    }

    public static void parseToItemLores(ManagerLanguage langman,
                                        ICommandSender sender,
                                        ILang lang,
                                        ItemStack itemStack) {
        parseToItemLores(langman, sender, lang, ((sen, langman1) -> {
        }), itemStack, false);
    }

    /**
     * Temporary method to be used until the {@link ItemStack#isSimilar(ItemStack)} method
     * is fixed. The method have hard time comparing the display name and lores,
     * so until it's fixed, manually compare them as String.
     *
     * @param item1
     * @param item2
     * @return
     */
    public static boolean areSimilar(ItemStack item1, ItemStack item2) {
        if (item1 == null && item2 == null)
            return true;

        if (item1 == null || item2 == null)
            return false;

        if (item1.getType() != item2.getType())
            return false;

        return areEqual(item1.getItemMeta(), item2.getItemMeta());
    }

    public static boolean loreEqual(List<String> lore1, List<String> lore2) {
        if (lore1 == null && lore2 == null)
            return true;

        if (lore1 == null || lore2 == null)
            return false;

        if (lore1.size() != lore2.size())
            return false;

        for (int i = 0; i < lore1.size(); i++) {
            if (!Objects.equals(lore1.get(i), lore2.get(i)))
                return false;
        }

        return true;
    }

    public static boolean areEqual(ItemMeta meta1, ItemMeta meta2) {
        if (meta1 == null && meta2 == null)
            return true;

        if (meta1 == null || meta2 == null)
            return false;

        if (meta1.getClass() != meta2.getClass())
            return false;

        // basic comparison
        if (!Objects.equals(meta1.getDisplayName(), meta2.getDisplayName()))
            return false;

        if (!loreEqual(meta1.getLore(), meta2.getLore()))
            return false;

        // make copy of meta and remove display name and lores
        // TODO: this is not required once the isSimilar method is fixed
        meta1 = meta1.clone();
        meta1.setDisplayName(null);
        meta1.setLore(null);

        meta2 = meta2.clone();
        meta2.setDisplayName(null);
        meta2.setLore(null);

        return Bukkit.getItemFactory().equals(meta1, meta2);
    }
}
