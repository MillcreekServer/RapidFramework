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
package io.github.wysohn.rapidframework.utils.locations;

import io.github.wysohn.rapidframework.main.FakePlugin;
import io.github.wysohn.rapidframework.pluginbase.objects.SimpleChunkLocation;
import io.github.wysohn.rapidframework.pluginbase.objects.SimpleLocation;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class LocationUtil {
    /*
     * public static void main(String[] ar){ while(true){ Scanner sc = new
     * Scanner(System.in); double x = Double.parseDouble(sc.nextLine()); double
     * y = Double.parseDouble(sc.nextLine());
     * 
     * double val = Math.atan2(y, x); val = val < 0 ? 2*Math.PI + val : val;
     * 
     * System.out.println("result: "+val); } }
     */

    // - : theta - yaw
    // + : theta - yaw - 2PI
    public static boolean isAttackFromBack(Entity target, Entity behind) {
        Location center = target.getLocation().clone();
        Location source = behind.getLocation().clone();

        float yaw = FakePlugin.nmsEntityManager.getYaw(target);
        yaw = (float) Math.toRadians(yaw);
        while (yaw > 2 * Math.PI)
            yaw -= 2 * Math.PI;
        while (yaw < 0)
            yaw += 2 * Math.PI;

        Location relativeCoord = source.subtract(center);
        double x = relativeCoord.getX();
        double z = relativeCoord.getZ();

        if (x == 0.0 && z == 0.0)
            return false;

        double theta = Math.atan2(x, z);
        theta = theta < 0 ? 2 * Math.PI + theta : theta;

        double finalVal = 2 * Math.PI - theta - yaw;
        finalVal = finalVal < 0 ? -finalVal : finalVal;

        double eyeAngle = Math.PI / 2 + Math.PI / 6;

        return finalVal > eyeAngle && finalVal < 2 * Math.PI - eyeAngle;
    }

    public static SimpleChunkLocation convertToSimpleChunkLocation(Chunk chunk) {
        return new SimpleChunkLocation(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public static SimpleLocation convertToSimpleLocation(Location loc) {
        return new SimpleLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
                loc.getYaw(), loc.getPitch());
    }

    public static Location convertToBukkitLocation(SimpleLocation from) {
        World world = Bukkit.getWorld(from.getWorld());
        if (world == null)
            return null;

        return new Location(world, from.getX(), from.getY(), from.getZ());
    }
}
