package io.github.wysohn.rapidframework2.core.manager.caching;

import java.util.Observable;

/**
 * This is an child of 'Observable' class, thus if any change happens in this class's instance,
 * it has to invoke 1.{@link Observable#setChanged()} and 2.{@link Observable#notifyObservers()}.
 */
public abstract class CachedElement<K> extends Observable {
    private final K key;

    public CachedElement(K key) {
        this.key = key;
    }

    /**
     * Key to be used when saved to database. It will be translated to String using toString() method.
     * @return the key. Never be null;
     */
    public K getKey(){
        return key;
    }

    /**
     * String version of key that is used in nameMap. It's different to getKey()
     * since it works as an alias for the key, not the actual key used to save it in the database.
     * Usually the display name of this instance but not necessarily that way.
     * @return alias key; null if not used.
     */
    protected String getStringKey(){
        return null;
    }


}
