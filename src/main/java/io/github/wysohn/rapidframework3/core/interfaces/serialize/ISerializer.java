package io.github.wysohn.rapidframework3.core.interfaces.serialize;

public interface ISerializer<T> {
    String serializeToString(T obj);

    T deserializeFromString(String json);
}
