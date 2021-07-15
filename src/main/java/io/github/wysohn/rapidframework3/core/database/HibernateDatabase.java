package io.github.wysohn.rapidframework3.core.database;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public abstract class HibernateDatabase<T extends CachedElement<?>> extends Database<T> {
    public static final String KEY = "key";

    private final Properties properties;
    private final SessionFactory factory;

    public HibernateDatabase(Class<T> type,
                             Properties properties) {
        super(type.getSimpleName(), type);
        this.properties = properties;

        // create session factory
        factory = new Configuration()
                .setProperties(properties)
                .addAnnotatedClass(type)
                .buildSessionFactory();
    }

    public HibernateDatabase(Class<T> type) {
        this(type, new Properties());
    }

    @Override
    public T load(String key) throws IOException {
        Transaction tx = null;
        T result = null;

        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            Query query = session.createQuery(String.format("FROM %s WHERE " + KEY + " = :key", tableName));
            query.setParameter(KEY, key);
            result = (T) query.getSingleResult();
            tx.commit();

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (tx != null)
                tx.rollback();
            return null;
        }
    }

    @Override
    public void save(String key, T obj) throws IOException {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            if (obj == null) {
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaDelete<T> delete = criteriaBuilder.createCriteriaDelete(type);
                Root<T> root = delete.from(type);
                delete.where(root.get(KEY).in(key));

                session.createQuery(delete).executeUpdate();
            } else {
                T state = obj.copy();
                session.saveOrUpdate(state);
            }

            tx.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (tx != null)
                tx.rollback();
        }
    }

    @Override
    public boolean has(String key) {
        try {
            return load(key) != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Set<String> getKeys() {
        Set<String> keys = new HashSet<>();

        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            Query query = session.createQuery("SELECT " + KEY + " FROM " + tableName);
            query.getResultList().stream()
                    .map(Object::toString)
                    .forEach(str -> keys.add((String) str));

            tx.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (tx != null)
                tx.rollback();
        }

        return keys;
    }

    @Override
    public void clear() {
        throw new RuntimeException("Do not use clear() on hibernate databases.");
    }


}