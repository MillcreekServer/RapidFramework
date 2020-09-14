package io.github.wysohn.rapidframework3.bukkit.utils;

import io.github.wysohn.rapidframework3.data.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BukkitLocationUtil {
    public static Location toLocation(SimpleLocation scloc){
        return new Location(Bukkit.getWorld(scloc.getWorld()), scloc.getX(), scloc.getY(), scloc.getZ());
    }
}
