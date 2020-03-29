package io.github.wysohn.rapidframework2.core.manager.caching;

import io.github.wysohn.rapidframework2.core.database.Database;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.tools.FileUtil;
import util.Validation;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public abstract class AbstractManagerElementCaching<K, V extends CachedElement<K>> extends PluginMain.Manager {
    private final ExecutorService saveTaskPool = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setPriority(Thread.NORM_PRIORITY - 1);
        return thread;
    });

    /*
     * Lock ordering should be cacheLock -> dbLock if it has to be nested.
     * Use locking only in public method to avoid confusion
     */
    private final Object cacheLock = new Object();
    private final Object dbLock = new Object();
    private final Database.DatabaseFactory<V> dbFactory;

    private Database<V> db;

    private final List<IObserver> observers = new ArrayList<IObserver>() {
        {
            add(new CachedElementObserver());
        }
    };

    private final Map<K, V> cachedElements = new HashMap<>();
    private final Map<String, K> nameMap = new HashMap<>();

    private IConstructionHandle<K, V> constructionHandle;

    public AbstractManagerElementCaching(int loadPriority) {
        super(loadPriority);
        dbFactory = createDatabaseFactory();
    }

    /**
     * Create DataFactory which will be used by this manager.
     * @return
     */
    protected abstract Database.DatabaseFactory<V> createDatabaseFactory();

    protected abstract K fromString(String string);

    protected abstract V newInstance(K key);

    @Override
    public void enable() throws Exception {

    }

    @Override
    public void load() throws Exception {
        synchronized (dbLock){
            db = dbFactory.getDatabase((String) main().conf().get("dbType").orElse("file"));
            Validation.assertNotNull(db);
        }

        synchronized (cacheLock){
            cachedElements.clear();
            nameMap.clear();
        }

        saveTaskPool.submit(()->{
            synchronized (dbLock){
                for(String keyStr : db.getKeys()){
                    V value = db.load(keyStr, null);
                    if (value != null){
                        K key = fromString(keyStr);

                        cache(key, value);
                    }
                }
            }
        }).get();

    }

    @Override
    public void disable() throws Exception {
        synchronized (cacheLock){
            synchronized (dbLock){
                main().getLogger().info("Waiting for the save tasks to be done...");
                saveTaskPool.shutdownNow().forEach(Runnable::run);
                main().getLogger().info("Save finished.");
            }
        }
    }

    public void setConstructionHandle(IConstructionHandle<K, V> constructionHandle) {
        this.constructionHandle = constructionHandle;
    }

    /**
     * Get number of elements exist in the cache table now.
     * @return cache size.
     */
    public int getCacheSize(){
        return cachedElements.size();
    }

    /**
     * Get data associated with 'name' String. This may not have any effect if the
     * Class used in the Template V does not implement getName() method correctly so the method always
     * return null. In this case, you have to use {@link #get(K)} instead.
     * @param name displayName to search for
     * @return The Optional of value. Optional.empty() if couldn't find it.
     */
    public Optional<WeakReference<V>> get(String name){
        synchronized (cacheLock){
            return get(nameMap.get(name));
        }
    }

    /**
     * get data associated with 'key' directly.
     * @param key the key
     * @return The Optional of value. Optional.empty() if couldn't find it.
     */
    public Optional<WeakReference<V>> get(K key){
        if(key == null)
            return Optional.empty();

        synchronized (cacheLock){
            if(cachedElements.containsKey(key)){
                return Optional.of(new WeakReference<>(cachedElements.get(key)));
            }

            //try load cache from db if cache is empty
            try {
                saveTaskPool.submit(() -> {
                    V loaded = null;
                    synchronized (dbLock) {
                        loaded = db.load(key.toString(), null);
                    }

                    if (loaded != null) {
                        cache(key, loaded);
                    }
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if(cachedElements.containsKey(key)){
                //at this point, the data really doesn't exist.
                return Optional.of(new WeakReference<>(cachedElements.get(key)));
            } else {
                return Optional.empty();
            }
        }
    }

    /**
     * Get value or create new instance provided by the supplier
     * @param key the key
     * @return the existing value or newly created one
     */
    public Optional<WeakReference<V>> getOrNew(K key){
        Validation.assertNotNull(key);

        V value = get(key)
                .map(Reference::get)
                .orElseGet(() -> newInstance(key));
        synchronized (cacheLock){
            cache(key, value);
        }

        return get(key);
    }

    private void cache(K key, V value) {
        observers.forEach(value::addObserver);
        cachedElements.put(key, value);

        if(value.getStringKey() != null && !nameMap.containsKey(value.getStringKey()))
            nameMap.put(value.getStringKey(), key);

        Optional.ofNullable(constructionHandle).ifPresent(handle -> handle.after(value));
    }

    public boolean setName(K key, String name){
        synchronized (cacheLock){
            if(nameMap.containsKey(name))
                return false;

            nameMap.put(name, key);
            return true;
        }
    }

    /**
     * Delete the name from nameMap
     * @param name name to delete
     */
    public void deleteName(String name){
        nameMap.remove(name);
    }

    /**
     * Delete the entry completely from both cache and database.
     * @param key associated key
     */
    public void delete(K key) {
        synchronized (cacheLock) {
            boolean result = deCache(key);

            saveTaskPool.submit(() -> {
                synchronized (dbLock){
                    db.save(key.toString(), null);
                }
            });
        }
    }

    /**
     * Remove the value associated 'key' but not from database.
     * This is useful if data in database has changed from different front-ends and
     * you want to reload the latest data from the database instead of using the cached data.
     * {@link #get(Object)} method will try to load data from the database if cache does not exist.
     * @param key the key to clear the cache
     * @return true if cleared; false if it wasn't available anyway.
     */
    public boolean deCache(K key){
        synchronized (cacheLock){
            V original = cachedElements.remove(key);
            if(original != null){
                observers.forEach(original::addObserver);
                if(original.getStringKey() != null){
                    nameMap.remove(original.getStringKey());
                }
            }
            return original != null;
        }
    }

    /**
     * Delete data from both cache and database and recreate a new instance.
     *
     * @param value value to reset
     */
    public void reset(V value) {
        delete(value.getKey());
        getOrNew(value.getKey());
    }

    public Set<K> keySet() {
        synchronized (cacheLock) {
            return new HashSet<>(cachedElements.keySet());
        }
    }

    public void forEach(Consumer<V> consumer) {
        forEach(consumer, false);
    }

    public void forEach(Consumer<V> consumer, boolean async) {
        if (async) {
            keySet().stream()
                    .map(this::get)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Reference::get)
                    .forEach(consumer);
        } else {
            synchronized (cacheLock) {
                keySet().stream()
                        .map(this::get)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(Reference::get)
                        .forEach(consumer);
            }
        }
    }

    protected <T> Database.DatabaseFactory<T> getDatabaseFactory(Class<T> clazz, String tablename) {
        return (dbType -> {
            try {
                switch (dbType) {
                    case "mysql":
                        return Database.Factory.build(clazz,
                                (String) main().conf().get("db.address").orElse("127.0.0.1"),
                                (String) main().conf().get("db.name").orElse(main().getPluginName()),
                                (String) main().conf().get("db.tablename").orElse(tablename),
                                (String) main().conf().get("db.username").orElse("root"),
                                (String) main().conf().get("db.password").orElse("1234"));
                    default:
                        return Database.Factory.build(clazz,
                                FileUtil.join(main().getPluginDirectory(), tablename));
                }
            } catch (Exception e) {
                e.printStackTrace();

                return Database.Factory.build(clazz,
                        FileUtil.join(main().getPluginDirectory(), tablename));
            }
        });
    }

    public interface IConstructionHandle<K, V extends CachedElement<K>>{
        /**
         * Called after the object is created. It can be useful if some data has to be filled
         * manually after the object is instantiated.
         * @param obj the object that was created.
         */
        void after(V obj);
    }

    private class CachedElementObserver implements IObserver {
        /**
         * Should be called only by Observable instances.
         *
         * @param observable
         */
        @Override
        public void update(ObservableElement observable) {
            V value = (V) observable;

            synchronized (cacheLock) {
                V cached = cachedElements.get(value.getKey());

                //ensure that the ObservableElement is the actual 'V'
                if (cached == value) {
                    saveTaskPool.submit(() -> {
                        synchronized (dbLock) {
                            db.save(cached.getKey().toString(), cached);
                        }
                    });
                } else {
                    throw new RuntimeException("Inconsistent cache detected. The cache x and the caller instance " +
                            "are not equivalent. Perhaps the caller instance is not discarded after reset()?");
                }
            }
        }
    }
}
