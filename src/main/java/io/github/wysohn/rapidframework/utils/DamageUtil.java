/*******************************************************************************
 *     Copyright (C) 2017 wysohn
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package io.github.wysohn.rapidframework.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class DamageUtil {
    /**
     * @deprecated damager is no longer needed
     * @param target
     * @param damager
     * @param damage
     */
    @Deprecated
    public static void damageNaturally(LivingEntity target, Entity damager, double damage) {
        int defensePoints = getDefensePoints(target.getEquipment());
        int toughness = getToughness(target.getEquipment());
        damage = damage * (1
                - Math.min(20.0, Math.max(defensePoints / 5.0, defensePoints - damage / (2 + toughness / 4.0))) / 25.0);
        target.damage(damage);
    }

    public static void damageNaturally(LivingEntity target, double damage) {
        int defensePoints = getDefensePoints(target.getEquipment());
        int toughness = getToughness(target.getEquipment());
        damage = damage * (1
                - Math.min(20.0, Math.max(defensePoints / 5.0, defensePoints - damage / (2 + toughness / 4.0))) / 25.0);
        target.damage(damage);
    }

    public static void damageNaturally(ItemStack using, LivingEntity target, Entity damager) {
        double damage = getWeaponDamage(using);
        damage += getExtraDamageForEnchantment(using, target.getType());

        int defensePoints = getDefensePoints(target.getEquipment());
        int toughness = getToughness(target.getEquipment());
        damage = damage * (1
                - Math.min(20.0, Math.max(defensePoints / 5.0, defensePoints - damage / (2 + toughness / 4.0))) / 25.0);
        target.damage(damage);
    }

    private static int getDefensePoints(EntityEquipment equip) {
        int sum = 0;

        for (ItemStack IS : equip.getArmorContents()) {
            if (IS == null)
                continue;

            Material mat = IS.getType();
            if (mat == Material.AIR)
                continue;

            if (!mat.name().contains("_"))
                continue;

            String[] split = mat.name().split("_");

            String material = split[0];
            String equipType = split[1];

            if (material.equals("LEATHER")) {
                if (equipType.equals("HELMET")) {
                    sum += 1;
                } else if (equipType.equals("CHESTPLATE")) {
                    sum += 3;
                } else if (equipType.equals("LEGGINGS")) {
                    sum += 2;
                } else if (equipType.equals("BOOTS")) {
                    sum += 1;
                } else {
                    // ?
                }
            } else if (material.equals("GOLD")) {
                if (equipType.equals("HELMET")) {
                    sum += 2;
                } else if (equipType.equals("CHESTPLATE")) {
                    sum += 5;
                } else if (equipType.equals("LEGGINGS")) {
                    sum += 3;
                } else if (equipType.equals("BOOTS")) {
                    sum += 1;
                } else {
                    // ?
                }
            } else if (material.equals("CHAIN")) {
                if (equipType.equals("HELMET")) {
                    sum += 2;
                } else if (equipType.equals("CHESTPLATE")) {
                    sum += 5;
                } else if (equipType.equals("LEGGINGS")) {
                    sum += 4;
                } else if (equipType.equals("BOOTS")) {
                    sum += 1;
                } else {
                    // ?
                }
            } else if (material.equals("IRON")) {
                if (equipType.equals("HELMET")) {
                    sum += 2;
                } else if (equipType.equals("CHESTPLATE")) {
                    sum += 6;
                } else if (equipType.equals("LEGGINGS")) {
                    sum += 5;
                } else if (equipType.equals("BOOTS")) {
                    sum += 2;
                } else {
                    // ?
                }
            } else if (material.equals("DIAMOND")) {
                if (equipType.equals("HELMET")) {
                    sum += 3;
                } else if (equipType.equals("CHESTPLATE")) {
                    sum += 8;
                } else if (equipType.equals("LEGGINGS")) {
                    sum += 6;
                } else if (equipType.equals("BOOTS")) {
                    sum += 3;
                } else {
                    // ?
                }
            } else {
                // ?
            }
        }

        return sum;
    }

    private static int getToughness(EntityEquipment equip) {
        int sum = 0;

        for (ItemStack IS : equip.getArmorContents()) {
            if (IS == null)
                continue;

            Material mat = IS.getType();
            String[] split = mat.name().split("_");

            String material = split[0];
            // String equipType = split[1];

            if (material.equals("DIAMOND")) {
                sum += 2;
            }
        }

        return sum;
    }

    /**
     * This does not check if tool will be broken.
     * 
     * @param tool
     */
    public static void consumeDurability(ItemStack tool) {
        int level = tool.getEnchantmentLevel(Enchantment.DURABILITY);
        if (level == 0) {
            tool.setDurability((short) (tool.getDurability() + 1));
        } else {
            if (Possibility.isWin(1.0D / (level + 1), 2))
                tool.setDurability((short) (tool.getDurability() + 1));
        }
    }

    /**
     *
     * @param tool
     * @return true if broken; false if not. <br>
     */
    public static boolean isBroken(ItemStack tool) {
        if (!tool.getType().name().endsWith("_AXE"))
            return false;

        return tool.getDurability() >= tool.getType().getMaxDurability();
    }

    public static double getWeaponDamage(ItemStack weapon) {
        Material mat = weapon.getType();

        String split[] = mat.name().split("_");
        if (split.length != 2)
            return 1.0;

        double damage = 1.0;

        String name = split[0];
        if (split[1].equals("SWORD")) {
            if (name.equals("WOOD")) {
                damage = 4.0;
            } else if (name.equals("GOLD")) {
                damage = 4.0;
            } else if (name.equals("STONE")) {
                damage = 5.0;
            } else if (name.equals("IRON")) {
                damage = 6.0;
            } else if (name.equals("DIAMOND")) {
                damage = 7.0;
            } else {
                damage = 1.0;
            }
        } else if (split[1].equals("AXE")) {
            if (name.equals("WOOD")) {
                damage = 7.0;
            } else if (name.equals("GOLD")) {
                damage = 7.0;
            } else if (name.equals("STONE")) {
                damage = 9.0;
            } else if (name.equals("IRON")) {
                damage = 9.0;
            } else if (name.equals("DIAMOND")) {
                damage = 9.0;
            } else {
                damage = 1.0;
            }
        } else {
            damage = 1.0;
        }

        return damage;
    }

    public static double getExtraDamageForEnchantment(ItemStack weapon, EntityType target) {
        int sharpness = weapon.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
        int smite = weapon.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD);
        int bane = weapon.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS);

        double damage = 0.0;

        if (sharpness > 0) {
            damage += 1;
            sharpness -= 1;

            damage += sharpness * 0.5;
        }

        switch (target) {
        case SKELETON:
        case ZOMBIE:
        case WITHER:
        case PIG_ZOMBIE:
            damage += smite * 2.5;
            break;
        case SPIDER:
        case CAVE_SPIDER:
        case SILVERFISH:
        case ENDERMITE:
            damage += bane * 2.5;
            break;
        default:
            break;
        }

        return damage;
    }
}
