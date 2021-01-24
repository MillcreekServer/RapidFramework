package io.github.wysohn.rapidframework3.core.database;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework3.utils.FileUtil;

import java.io.File;
import java.sql.SQLException;

public class Databases {
    public static <T extends CachedElement<?>> Database<T> build(String name,
                                                                 File folder,
                                                                 ISerializer serializer,
                                                                 Class<T> type) {
        if (!folder.exists())
            folder.mkdirs();

        return new DatabaseFile<>(
                serializer,
                name,
                type,
                "json",
                FileUtil::readFromFile,
                FileUtil::writeToFile,
                FileUtil.join(folder, name));
    }

    public static <T extends CachedElement<?>> Database<T> build(String address,
                                                                 String dbName,
                                                                 String tablename,
                                                                 String username,
                                                                 String password) throws SQLException {
        //return new DatabaseMysql(address, dbName, tablename, username, password);
        throw new RuntimeException(); //TODO
    }

    @FunctionalInterface
    public interface DatabaseFactory<V extends CachedElement<?>> {
        Database<V> createDatabase(ISerializer serializer,
                                   Class<V> objType,
                                   String dbType);
    }
}
