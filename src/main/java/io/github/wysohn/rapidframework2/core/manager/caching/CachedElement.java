package io.github.wysohn.rapidframework2.core.manager.caching;

/**
 * For Gson to serialize/deserialize this object, the child class must have no-args
 * constructor which will call the parent constructor. Gson always use the no-args constructor of the
 * very child's no-arg constructor, and if it doesn't exist, Gson will skip any constructors and straight up
 * to use unsafe allocator. It's okay to pass 'null' for the parent class's constructor since Gson will later
 * fill up the fields.
 *
 * This is also an child of 'Observable' class, thus if any change happens in this class's instance,
 * it has to invoke {@link ObservableElement#notifyObservers()}.
 */
public abstract class CachedElement<K> extends ObservableElement {
    private final K key;

    public CachedElement(K key) {
        super();

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
