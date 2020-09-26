package io.github.wysohn.rapidframework3.core.database;

import io.github.wysohn.rapidframework3.utils.FileUtil;

import java.io.File;
import java.sql.SQLException;

public class Databases {
    public static Database build(String name, File folder) {
        if (!folder.exists())
            folder.mkdirs();

        return new DatabaseFile(name,
                "json",
                FileUtil::readFromFile,
                FileUtil::writeToFile,
                FileUtil.join(folder, name));
    }

    public static Database build(String address,
                                 String dbName,
                                 String tablename,
                                 String username,
                                 String password) throws SQLException {
        return new DatabaseMysql(address, dbName, tablename, username, password);
    }

    @FunctionalInterface
    public interface DatabaseFactory {
        Database getDatabase(String dbType);
    }
}
