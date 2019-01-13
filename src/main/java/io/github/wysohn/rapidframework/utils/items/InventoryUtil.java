package io.github.wysohn.rapidframework.utils.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {
    public static boolean consumeOneItemInHand(Player player) {
        ItemStack IS = player.getItemInHand();
        if(IS == null || IS.getType() == Material.AIR)
            return false;

        if(IS.getAmount() > 1) {
            IS.setAmount(IS.getAmount() - 1);
            player.setItemInHand(IS);
        }else {
            player.setItemInHand(null);
        }

        return true;
    }
}
