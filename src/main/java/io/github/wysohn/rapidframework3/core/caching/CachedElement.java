package io.github.wysohn.rapidframework3.core.caching;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * For Gson to serialize/deserialize this object, the child class must have no-args
 * constructor which will call the parent constructor. Gson always use the no-args constructor of the
 * very child's no-arg constructor, and if it doesn't exist, Gson will skip any constructors and straight up
 * to use unsafe allocator. It's okay to pass 'null' for the parent class's constructor since Gson will later
 * fill up the fields.
 * <p>
 * This is also an child of 'Observable' class, thus if any change happens in this class's instance,
 * it has to invoke {@link AbstractManagerElementCaching.ObservableElement#notifyObservers()}.
 */
@Entity
@Table
public abstract class CachedElement<K> extends AbstractManagerElementCaching.ObservableElement {
    @Column
    private final K key;

    @Column
    private String stringKey;

    public CachedElement(K key) {
        super();

        this.key = key;
    }

    /**
     * Key to be used when saved to database. It will be translated to String using toString() method.
     *
     * @return the key. Never be null;
     */
    public K getKey() {
        return key;
    }

    public final String getStringKey() {
        return stringKey;
    }

    /**
     * Update the stringKey of this instance. This is immediately reflected with the manager which
     * is holding this instance.
     *
     * @param stringKey the new key to update. Providing null or empty String will just delete
     *                  the stringKey mapping.
     */
    public final void setStringKey(String stringKey) {
        this.stringKey = stringKey;

        notifyObservers();
    }
}
