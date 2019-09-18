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
package io.github.wysohn.rapidframework.database.serialize;

import copy.com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

public class LocationSerializer implements Serializer<Location> {
    @Override
    public JsonElement serialize(Location arg0, Type arg1, JsonSerializationContext arg2) {
        JsonObject json = new JsonObject();

        // return empty if world does not exists
        if (arg0.getWorld() == null) {
            return json;
        }

        json.add("world", arg2.serialize(arg0.getWorld().getName(), String.class));
        json.add("x", arg2.serialize(arg0.getX(), Double.class));
        json.add("y", arg2.serialize(arg0.getY(), Double.class));
        json.add("z", arg2.serialize(arg0.getZ(), Double.class));
        json.add("pitch", arg2.serialize(arg0.getPitch(), Float.class));
        json.add("yaw", arg2.serialize(arg0.getYaw(), Float.class));

        return json;
    }

    @Override
    public Location deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
            throws JsonParseException {
        JsonObject json = (JsonObject) arg0;

        JsonElement worldElem = json.get("world");
        if (worldElem == null)
            return null;

        String worldName = worldElem.getAsString();
        World world = Bukkit.getWorld(worldName);
        if (world == null)
            return null;

        double x = json.get("x").getAsDouble();
        double y = json.get("y").getAsDouble();
        double z = json.get("z").getAsDouble();
        float pitch = arg2.deserialize(json.has("pitch") ? json.get("pitch") : new JsonPrimitive(0.0f), Float.class);
        float yaw = arg2.deserialize(json.has("yaw") ? json.get("yaw") : new JsonPrimitive(0.0f), Float.class);

        return new Location(world, x, y, z, pitch, yaw);
    }

}
