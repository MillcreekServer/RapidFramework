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
package io.github.wysohn.rapidframework.database;

import copy.com.google.gson.Gson;
import copy.com.google.gson.GsonBuilder;
import io.github.wysohn.rapidframework.database.serialize.*;
import io.github.wysohn.rapidframework.pluginbase.constants.SimpleChunkLocation;
import io.github.wysohn.rapidframework.pluginbase.constants.SimpleLocation;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;

public abstract class Database<T> {
    private static GsonBuilder builder = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC).enableComplexMapKeySerialization()
            .registerTypeAdapter(Location.class, new LocationSerializer())
            .registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
            .registerTypeAdapter(ItemStack[].class, new ItemStackArraySerializer())
            .registerTypeAdapter(UUID.class, new UUIDSerializer())
            .registerTypeAdapter(SimpleLocation.class, new DefaultSerializer<SimpleLocation>())
            .registerTypeAdapter(SimpleChunkLocation.class, new DefaultSerializer<SimpleChunkLocation>());

    public static void registerTypeAdapter(Class<?> clazz, Object obj) {
        synchronized (builder) {
            builder.registerTypeAdapter(clazz, obj);
            // Bukkit.getLogger().info("Serializer --
            // ["+clazz.getSimpleName()+", "+obj+"]");
        }
    }

    /**
     * Deserialize the data from the database and return
     * 
     * @param key
     *            the key of the data
     * @param def
     *            default value to be used if data was not found.
     * @return the deserialized data
     */
    public abstract T load(String key, T def);

    /**
     * Serialize the data and put it into the database.
     * 
     * @param key
     *            the key to pair the data with
     * @param value
     *            the data to be saved
     */
    public abstract void save(String key, T value);

    /**
     * Check if the key exists in the database
     * 
     * @param key
     *            the key to check
     * @return true if exists; false if not
     */
    public abstract boolean has(String key);

    /**
     * get list of all keys in this database. The operation time of this method
     * can be longer depends on the amount of data saved in the data. Make sure
     * to use it asynchronous manner or only once on initialization.
     *
     * @return
     */
    public abstract Set<String> getKeys();

    /**
     * Clear all data in the database. <b> Use it carefully as it will
     * immediately clear up the database</b>
     */
    public abstract void clear();

    private Gson gson;

    /**
     * Serialize the object using the class type of object itself.
     * 
     * @param obj
     * @return serialized string
     */
    public String serialize(Object obj) {
        if (gson == null)
            gson = builder.create();

        return gson.toJson(obj);
    }

    /**
     * Serialize the object using specified class type.
     * 
     * @param obj
     * @param clazz
     * @return serialzied string
     */
    public String serialize(Object obj, Type clazz) {
        if (gson == null)
            gson = builder.create();

        return gson.toJson(obj, clazz);
    }

    /**
     * Deserialize the serialized string into the specified type of object.
     * 
     * @param ser
     * @param clazz
     * @return deserialized object
     */
    public Object deserialize(String ser, Type clazz) {
        if (gson == null)
            gson = builder.create();

        return gson.fromJson(ser, clazz);
    }

}
