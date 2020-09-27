package io.github.wysohn.rapidframework3.core.serialize;

import copy.com.google.gson.*;
import io.github.wysohn.rapidframework3.interfaces.serialize.CustomAdapter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BukkitConfigurationSerializer implements CustomAdapter<ConfigurationSerializable> {
    @Override
    public ConfigurationSerializable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject ser = (JsonObject) json;

        // ignore Map without SERIALIZED_TYPE_KEY (they are simple map in such case)
        if (ser.get(ConfigurationSerialization.SERIALIZED_TYPE_KEY) == null)
            return null;

        Map<String, Object> map = new LinkedHashMap<>();
        map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY,
                ser.get(ConfigurationSerialization.SERIALIZED_TYPE_KEY).getAsString());

        try {
            Map<String, ConfigurationSerializable> subs = new HashMap<>();
            ser.entrySet().forEach(entry -> {
                String key = entry.getKey();
                JsonElement value = entry.getValue();

                if (value instanceof JsonObject) {
                    ConfigurationSerializable sub = deserialize(value, typeOfT, context);
                    if (sub == null) { // just a Map if JsonObject and is not serialized value
                        subs.put(key, context.deserialize(value, Map.class));
                    } else {
                        subs.put(key, sub);
                    }
                } else {
                    subs.put(key, context.deserialize(value, Object.class));
                }
            });
            map.putAll(subs);

            return ConfigurationSerialization.deserializeObject(map);
        } catch (Exception ex) {
            throw new RuntimeException("Cannot deserialize " + json, ex);
        }
    }

    private Map<String, Object> flatConfiguration(ConfigurationSerializable serializable) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY,
                ConfigurationSerialization.getAlias(serializable.getClass()));
        serializable.serialize().forEach((key, val) -> {
            if (val instanceof ConfigurationSerializable) {
                map.put(key, flatConfiguration((ConfigurationSerializable) val));
            } else {
                map.put(key, val);
            }
        });
        return map;
    }

    @Override
    public JsonElement serialize(ConfigurationSerializable src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject ser = new JsonObject();
        ser.addProperty(SER_KEY, ConfigurationSerializable.class.getName());
        ser.add(SER_VALUE, context.serialize(flatConfiguration(src)));
        return ser;
    }

    private static final String SER_KEY = "$serkey";
    private static final String SER_VALUE = "$serval";
}