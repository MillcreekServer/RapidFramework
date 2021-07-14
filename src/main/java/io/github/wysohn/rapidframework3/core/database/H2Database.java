package io.github.wysohn.rapidframework3.core.database;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;

import java.util.Properties;

public abstract class H2Database<T extends CachedElement<?>> extends HibernateDatabase<T> {
    public H2Database(Class<T> type,
                      String url,
                      String userName,
                      String password) {
        super(type, new Properties() {{
            put("connection.url", url);
            put("connection.username", userName);
            put("connection.password", password);
        }});
    }
}
