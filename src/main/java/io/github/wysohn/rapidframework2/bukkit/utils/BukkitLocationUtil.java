package io.github.wysohn.rapidframework2.bukkit.utils;

import io.github.wysohn.rapidframework2.core.objects.location.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BukkitLocationUtil {
    public static Location toLocation(SimpleLocation scloc){
        return new Location(Bukkit.getWorld(scloc.getWorld()), scloc.getX(), scloc.getY(), scloc.getZ());
    }
}
