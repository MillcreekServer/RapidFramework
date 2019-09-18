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
package io.github.wysohn.rapidframework.utils.strings;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class LoreOrganizer {
    private static final int MAX = 30;

    public static List<String> organize(List<String> lores) {
        List<String> newLore = new ArrayList<String>();

        String color = "§f";
        String str = "";
        for (String lore : lores) {
            String[] sentences = ChatColor.stripColor(lore).split(" ");
            if (lore.startsWith("§")) {
                color = lore.substring(0, 2);
            }

            int index = 0;
            for (String sentence : sentences) {

                if ((str + " " + sentence).length() <= MAX) {
                    str += index == 0 ? sentence : " " + sentence;
                } else {
                    newLore.add(color + str);
                    str = sentence;
                }
                index++;
            }

            newLore.add(color + str);
            str = "";
        }

        return newLore;
    }

    public static List<String> organize(String[] lores) {
        List<String> newLore = new ArrayList<String>();

        String color = "§f";
        String str = "";
        for (String lore : lores) {
            String[] sentences = ChatColor.stripColor(lore).split(" ");
            if (lore.startsWith("§")) {
                color = lore.substring(0, 2);
            }

            int index = 0;
            for (String sentence : sentences) {

                if ((str + " " + sentence).length() <= MAX) {
                    str += index == 0 ? sentence : " " + sentence;
                } else {
                    newLore.add(color + str);
                    str = sentence;
                }
                index++;
            }

            newLore.add(color + str);
            str = "";
        }

        return newLore;
    }

    public static List<String> organize(String lore) {
        List<String> newLore = new ArrayList<String>();

        String color = "§f";
        String str = "";
        String[] sentences = ChatColor.stripColor(lore).split(" ");
        if (lore.startsWith("§")) {
            color = lore.substring(0, 2);
        }

        int index = 0;
        for (String sentence : sentences) {

            if ((str + " " + sentence).length() <= MAX) {
                str += index == 0 ? sentence : " " + sentence;
            } else {
                newLore.add(color + str);
                str = sentence;
            }
            index++;
        }

        newLore.add(color + str);
        str = "";

        return newLore;
    }
}
