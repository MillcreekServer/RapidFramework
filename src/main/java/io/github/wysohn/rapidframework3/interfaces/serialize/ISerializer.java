package io.github.wysohn.rapidframework3.interfaces.serialize;

public interface ISerializer {
    String serializeToString(Class<?> type, Object obj);

    <T> T deserializeFromString(Class<T> type, String json) throws Exception;
}
