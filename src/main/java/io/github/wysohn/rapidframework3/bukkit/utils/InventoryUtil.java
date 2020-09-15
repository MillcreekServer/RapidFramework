package io.github.wysohn.rapidframework3.bukkit.utils;

import io.github.wysohn.rapidframework3.core.language.ManagerLanguage;
import io.github.wysohn.rapidframework3.interfaces.ICommandSender;
import io.github.wysohn.rapidframework3.interfaces.language.ILang;
import io.github.wysohn.rapidframework3.interfaces.language.ILangParser;
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
}
