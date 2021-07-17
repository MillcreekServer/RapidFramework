package io.github.wysohn.rapidframework4.core.database.hibernate;

import io.github.wysohn.rapidframework4.core.caching.CachedElement;

import java.util.Properties;

public abstract class H2Database<K, T extends CachedElement<K>> extends HibernateDatabase<K, T> {
    public H2Database(Class<T> type,
                      String url,
                      String userName,
                      String password) {
        super(type, new Properties() {{
            put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            put("hibernate.connection.url", url);
            put("hibernate.connection.username", userName);
            put("hibernate.connection.password", password);
        }});
    }
}
