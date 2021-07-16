package io.github.wysohn.rapidframework3.core.database;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;

import java.util.Properties;

public class MySQLDatabase<K, V extends CachedElement<K>> extends HibernateDatabase<K, V>{
    /**
     * A MySQL hibernate session.
     *
     * It uses the SimpleClassName of the 'type' provided as the table name
     * @param type
     * @param address
     * @param database
     * @param userName
     * @param password
     */
    public MySQLDatabase(Class<V> type,
                         String address,
                         String database,
                         String tableName,
                         String userName,
                         String password) {
        super(tableName, type, new Properties() {{
            put("hibernate.connection.url", "jdbc:mysql://"+address+"/"+database);
            put("hibernate.connection.username", userName);
            put("hibernate.connection.password", password);
        }});
    }
}
