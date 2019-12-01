package io.github.wysohn.rapidframework2.core.database.serialize;

import copy.com.google.gson.*;
import io.github.wysohn.rapidframework2.core.interfaces.serialize.Serializer;

import java.lang.reflect.Type;

public class DefaultSerializer<T> implements Serializer<T> {
    private static final Gson gson = new Gson();

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return gson.fromJson(json, typeOfT);
    }

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        return gson.toJsonTree(src, typeOfSrc);
    }
}
