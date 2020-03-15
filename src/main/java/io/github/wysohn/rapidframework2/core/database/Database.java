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
package io.github.wysohn.rapidframework2.core.database;

import copy.com.google.gson.Gson;
import copy.com.google.gson.GsonBuilder;
import copy.com.google.gson.JsonSyntaxException;
import copy.com.google.gson.TypeAdapter;
import copy.com.google.gson.internal.bind.TypeAdapters;
import copy.com.google.gson.stream.JsonReader;
import copy.com.google.gson.stream.JsonToken;
import copy.com.google.gson.stream.JsonWriter;
import io.github.wysohn.rapidframework2.core.database.file.DatabaseFile;
import io.github.wysohn.rapidframework2.core.database.mysql.DatabaseMysql;
import io.github.wysohn.rapidframework2.core.database.serialize.DefaultSerializer;
import io.github.wysohn.rapidframework2.core.database.serialize.UUIDSerializer;
import io.github.wysohn.rapidframework2.core.manager.caching.ObservableElement;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleChunkLocation;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleLocation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

public abstract class Database<T> {
    private static final TypeAdapter<String> NULL_ADOPTER_STRING = new TypeAdapter<String>() {

        @Override
        public void write(JsonWriter out, String value) throws IOException {
            out.value(value);
        }

        @Override
        public String read(JsonReader in) throws IOException {
            JsonToken token = in.peek();
            if (token == JsonToken.NULL) {
                in.nextNull();
                return "";
            } else if (token == JsonToken.NUMBER || token == JsonToken.STRING) {
                return in.nextString();
            } else {
                throw new JsonSyntaxException(token + " is not valid value for String!");
            }
        }

    };
    private static final TypeAdapter<Boolean> NULL_ADOPTER_BOOLEAN = new TypeAdapter<Boolean>() {

        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            out.value(value);
        }

        @Override
        public Boolean read(JsonReader in) throws IOException {
            JsonToken token = in.peek();
            if (token == JsonToken.NULL) {
                in.nextNull();
                return false;
            } else if (token == JsonToken.BOOLEAN) {
                return in.nextBoolean();
            } else {
                throw new JsonSyntaxException(token + " is not valid value for Boolean!");
            }
        }

    };
    private static final TypeAdapter<Number> NULL_ADOPTER_NUMBER = new TypeAdapter<Number>() {

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
                out.value(0);
            } else {
                out.value(value);
            }
        }

        @Override
        public Number read(JsonReader in) throws IOException {
            JsonToken token = in.peek();

            if (token == JsonToken.NULL) {
                in.nextNull();
                return 0;
            } else if (token == JsonToken.NUMBER) {
                String value = in.nextString();
                if (value.contains("."))
                    return Double.parseDouble(value);
                else
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException ex) {
                        return Long.parseLong(value);
                    }
            } else {
                throw new JsonSyntaxException(token + " is not valid value for Number!");
            }
        }

    };

    private static final TypeAdapter<Float> NULL_ADOPTER_FLOAT = new TypeAdapter<Float>() {

        @Override
        public void write(JsonWriter out, Float value) throws IOException {
            if (value == null) {
                out.value(0);
            } else {
                out.value(value);
            }
        }

        @Override
        public Float read(JsonReader in) throws IOException {
            JsonToken token = in.peek();

            if (token == JsonToken.NULL) {
                in.nextNull();
                return 0f;
            } else if (token == JsonToken.NUMBER) {
                String value = in.nextString();
                return Float.parseFloat(value);
            } else {
                throw new JsonSyntaxException(token + " is not valid value for Float!");
            }
        }

    };
    private static GsonBuilder builder = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .serializeNulls().registerTypeAdapterFactory(TypeAdapters.newFactory(String.class, NULL_ADOPTER_STRING))
            .registerTypeAdapterFactory(TypeAdapters.newFactory(boolean.class, Boolean.class, NULL_ADOPTER_BOOLEAN))
            .registerTypeAdapterFactory(TypeAdapters.newFactory(int.class, Integer.class, NULL_ADOPTER_NUMBER))
            .registerTypeAdapterFactory(TypeAdapters.newFactory(long.class, Long.class, NULL_ADOPTER_NUMBER))
            .registerTypeAdapterFactory(TypeAdapters.newFactory(float.class, Float.class, NULL_ADOPTER_FLOAT))
            .registerTypeAdapterFactory(TypeAdapters.newFactory(double.class, Double.class, NULL_ADOPTER_NUMBER))
            .registerTypeAdapter(UUID.class, new UUIDSerializer())
            .registerTypeAdapter(ObservableElement.class, new DefaultSerializer<ObservableElement>())
            .registerTypeAdapter(SimpleLocation.class, new DefaultSerializer<SimpleLocation>())
            .registerTypeAdapter(SimpleChunkLocation.class, new DefaultSerializer<SimpleChunkLocation>());

    public Database(Class<T> type, String tableName) {
        super();
        this.type = type;
        this.tableName = tableName;
    }

    protected final Class<T> type;
    protected final String tableName;

    public static void registerTypeAdapter(Class<?> clazz, Object obj) {
        synchronized (builder) {
            builder.registerTypeAdapter(clazz, obj);
            // Bukkit.getLogger().info("Serializer --
            // ["+clazz.getSimpleName()+", "+obj+"]");
        }
    }

    public String getTableName() {
        return tableName;
    }

    /**
     * Deserialize the data from the database and return
     *
     * @param key the key of the data
     * @param def default value to be used if data was not found.
     * @return the deserialized data
     */
    public abstract T load(String key, T def);

    /**
     * Serialize the data and put it into the database.
     *
     * @param key   the key to pair the data with
     * @param value the data to be saved
     */
    public abstract void save(String key, T value);

    /**
     * Check if the key exists in the database
     *
     * @param key the key to check
     * @return true if exists; false if not
     */
    public abstract boolean has(String key);

    /**
     * get list of all keys in this database. The operation time of this method can
     * be longer depends on the amount of data saved in the data. Make sure to use
     * it asynchronous manner or only once on initialization.
     *
     * @return
     */
    public abstract Set<String> getKeys();

    /**
     * Clear all data in the database. <b> Use it carefully as it will immediately
     * clear up the database</b>
     */
    public abstract void clear();

    private Gson gson;

    /**
     * Serialize the object using the class type of object itself.
     *
     * @param obj
     * @return serialized string
     */
    protected String serialize(Object obj) {
        return serialize(obj, obj.getClass());
    }

    /**
     * Serialize the object using specified class type.
     *
     * @param obj
     * @param clazz
     * @return serialzied string
     */
    protected String serialize(Object obj, Type clazz) {
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
    protected <R> R deserialize(String ser, Class<R> clazz) {
        if (gson == null)
            gson = builder.create();

        return gson.fromJson(ser, clazz);
    }

    @FunctionalInterface
    public interface DatabaseFactory<V> {
        Database<V> getDatabase(String dbType);
    }

    public static class Factory {
        private static <V> void assertType(Class<V> clazz) {
            try {
                clazz.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new AssertionError(clazz + " does not have no-args constructor, so Gson will not be " +
                        "able to properly serialize/deserialize it.");
            }
        }

        public static <V> Database<V> build(Class<V> type, File folder) {
            assertType(type);

            if(!folder.exists())
                folder.mkdirs();

            return new DatabaseFile<>(type, folder);
        }

        public static <V> Database<V> build(Class<V> type,
                                            String address,
                                            String dbName,
                                            String tablename,
                                            String username,
                                            String password) throws SQLException {
            assertType(type);

            return new DatabaseMysql<>(type, address, dbName, tablename, username, password);
        }
    }
}
