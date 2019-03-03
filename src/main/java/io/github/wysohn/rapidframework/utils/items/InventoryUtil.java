package io.github.wysohn.rapidframework.utils.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {
    public static boolean consumeOneItemInHand(Player player) {
	ItemStack IS = player.getItemInHand();
	if (IS == null || IS.getType() == Material.AIR)
	    return false;

	if (IS.getAmount() > 1) {
	    IS.setAmount(IS.getAmount() - 1);
	    player.setItemInHand(IS);
	} else {
	    player.setItemInHand(null);
	}

	return true;
    }

    public static boolean isEquipment(ItemStack IS) {
	if (IS == null)
	    return false;

	Material mat = IS.getType();
	String matName = mat.name();

	return matName.endsWith("_HELMET") || matName.endsWith("_CHESTPLATE") || matName.endsWith("_LEGGINGS")
		|| matName.endsWith("_BOOTS");
    }

    /**
     * Get raw slot index of given equipment
     * 
     * @param IS
     * @return 5, 6, 7, or 8 corresponding to helmet, chest, legs, and boots. -1 if
     *         not equipment.
     */
    public static int getRawSlotOfEquipment(ItemStack IS) {
	if (IS == null)
	    return -1;

	switch (IS.getType()) {
	case LEATHER_HELMET:
	case IRON_HELMET:
	case GOLD_HELMET:
	case DIAMOND_HELMET:
	case CHAINMAIL_HELMET:
	    return 5;
	case LEATHER_CHESTPLATE:
	case IRON_CHESTPLATE:
	case GOLD_CHESTPLATE:
	case DIAMOND_CHESTPLATE:
	case CHAINMAIL_CHESTPLATE:
	    return 6;
	case LEATHER_LEGGINGS:
	case IRON_LEGGINGS:
	case GOLD_LEGGINGS:
	case DIAMOND_LEGGINGS:
	case CHAINMAIL_LEGGINGS:
	    return 7;
	case LEATHER_BOOTS:
	case IRON_BOOTS:
	case GOLD_BOOTS:
	case DIAMOND_BOOTS:
	case CHAINMAIL_BOOTS:
	    return 8;
	default:
	    return -1;
	}
    }
}
