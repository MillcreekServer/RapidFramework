package io.github.wysohn.rapidframework2.core.manager.caching;

import io.github.wysohn.rapidframework2.core.database.Database;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.tools.FileUtil;
import util.Validation;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    private Database.DatabaseFactory<V> dbFactory;
    private Database<V> db;

    private final List<IObserver> observers = new ArrayList<IObserver>() {
        {
            add(new CachedElementObserver());
        }
    };

    private final Map<K, V> cachedElements = new HashMap<>();

    private final Map<String, K> nameToKeyMap = new HashMap<>();
    private final Map<K, String> keyToNameMap = new HashMap<>();

    private IConstructionHandle<K, V> constructionHandle;

    public AbstractManagerElementCaching(int loadPriority) {
        super(loadPriority);
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
        dbFactory = createDatabaseFactory();

        // prevent any other works before initializing caches
        synchronized (cacheLock) {
            // wait for previous save tasks to finish before instantiating new database
            saveTaskPool.submit(() -> {
                db = dbFactory.getDatabase((String) main().conf().get("dbType").orElse("file"));
                Validation.assertNotNull(db);
            }).get();

            for (String keyStr : db.getKeys()) {
                V value = db.load(keyStr, null);
                if (value != null) {
                    K key = fromString(keyStr);

                    cache(key, value);
                    Optional.ofNullable(constructionHandle).ifPresent(handle -> handle.after(value));
                }
            }
        }
    }

    @Override
    public void load() throws Exception {

    }

    @Override
    public void disable() throws Exception {
        synchronized (cacheLock) {
            main().getLogger().info("Waiting for the save tasks to be done...");
            List<Runnable> notDone = new ArrayList<>(saveTaskPool.shutdownNow());
            saveTaskPool.awaitTermination(30, TimeUnit.SECONDS);  // wait for running tasks to finish
            notDone.forEach(Runnable::run); // manually run all queued tasks
            main().getLogger().info("Save finished.");
        }
    }

    public void setConstructionHandle(IConstructionHandle<K, V> constructionHandle) {
        this.constructionHandle = constructionHandle;
    }

    /**
     * Get number of elements exist in the cache table now.
     *
     * @return cache size.
     */
    public int getCacheSize() {
        return cachedElements.size();
    }

    private void handleDBOperationFailure(String key, Throwable throwable) {
        throwable.printStackTrace();

        main().getLogger().severe("Key: " + key);
        main().getLogger().severe("Manager: " + getClass().getSimpleName());

        // At this point, irreversible data corruption can happen, so it's safer to turn off the plugin.
        main().shutdown();
    }

    /**
     * Get data associated with 'name' String. This may not have any effect if the
     * Class used in the Template V does not implement getName() method correctly so the method always
     * return null. In this case, you have to use {@link #get(K)} instead.
     *
     * @param name displayName to search for
     * @return The Optional of value. Optional.empty() if couldn't find it.
     */
    public Optional<WeakReference<V>> get(String name) {
        synchronized (cacheLock) {
            return get(nameToKeyMap.get(name));
        }
    }

    /**
     * get data associated with 'key' directly.
     * @param key the key
     * @return The Optional of value. Optional.empty() if couldn't find it.
     */
    public Optional<WeakReference<V>> get(K key) {
        if (key == null)
            return Optional.empty();

        synchronized (cacheLock) {
            if (cachedElements.containsKey(key)) {
                return Optional.of(new WeakReference<>(cachedElements.get(key)));
            }
        }

        V value = null;
        synchronized (cacheLock) {
            //try load cache from db if cache is empty
            try {
                saveTaskPool.submit((Callable<Void>) () -> {
                    Validation.assertNotNull(db, "Key was " + key);
                    V loaded = db.load(key.toString(), null);

                    if (loaded != null) {
                        AbstractManagerElementCaching.this.cache(key, loaded);
                    }

                    return null;
                }).get();
            } catch (ExecutionException e) {
                handleDBOperationFailure(key.toString(), e);
            } catch (InterruptedException e) {
                // ignore
            }

            value = cachedElements.get(key);
        }

        if (value != null) {
            V finalValue = value;
            Optional.ofNullable(constructionHandle).ifPresent(handle -> handle.after(finalValue));
        }

        return Optional.ofNullable(value).map(WeakReference::new);
    }

    /**
     * Get value or create new instance provided by the supplier
     * @param key the key
     * @return the existing value or newly created one
     */
    public Optional<WeakReference<V>> getOrNew(K key) {
        Validation.assertNotNull(key);

        V value = get(key)
                .map(Reference::get)
                .orElse(null);

        if (value == null) {
            value = newInstance(key);
        }

        synchronized (cacheLock) {
            cache(key, value);
        }

        V finalValue = value;
        Optional.ofNullable(constructionHandle).ifPresent(handle -> handle.after(finalValue));

        return get(key);
    }

    private void cache(K key, V value) {
        observers.forEach(value::addObserver);
        cachedElements.put(key, value);

        String oldName = keyToNameMap.get(key);
        if (!Objects.equals(oldName, value.getStringKey())) {
            keyToNameMap.remove(key);
            if (oldName != null)
                nameToKeyMap.remove(oldName);

            if (value.getStringKey() != null && value.getStringKey().trim().length() > 0) {
                keyToNameMap.put(key, value.getStringKey());
                nameToKeyMap.put(value.getStringKey(), key);
            }
        }
    }

    /**
     * Delete the entry completely from both cache and database.
     * @param key associated key
     */
    public void delete(K key) {
        synchronized (cacheLock) {
            boolean result = deCache(key);

            saveTaskPool.submit(() -> {
                try {
                    db.save(key.toString(), null);
                } catch (IOException e) {
                    e.printStackTrace();
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
            if(original != null) {
                observers.forEach(original::removeObserver);

                String name = keyToNameMap.remove(key);
                if (name != null)
                    nameToKeyMap.remove(name);
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

    public void forEach(Consumer<? super V> consumer) {
        synchronized (cacheLock) {
            keySet().stream()
                    .map(this::get)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Reference::get)
                    .forEach(consumer);
        }
    }

    /**
     * Search for cache, which the value V confirms with the 'predicate'. It is a simple Linear search
     * (O(n)) algorithm, so there can be overhead using this method.
     *
     * @param predicate filtering predicate.
     * @return List of values which passes the 'predicate'
     */
    public List<V> search(Predicate<V> predicate) {
        synchronized (cacheLock) {
            return cachedElements.values().stream()
                    .filter(predicate)
                    .collect(Collectors.toList());
        }
    }

    public List<IObserver> getObservers() {
        return Collections.unmodifiableList(observers);
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
                handleDBOperationFailure(tablename, e);
                return null;
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
                cache(value.getKey(), value);

                saveTaskPool.submit(() -> {
                    try {
                        db.save(value.getKey().toString(), value);
                    } catch (Exception e) {
                        handleDBOperationFailure(value.getKey().toString(), e);
                    }
                });
            }
        }
    }

    public abstract static class ObservableElement {
        private transient final List<IObserver> observers;

        public ObservableElement() {
            observers = new LinkedList<>();
        }

        void addObserver(IObserver observer) {
            if (observer == null)
                throw new NullPointerException("Observer cannot be null");

            if (!observers.contains(observer)) {
                observers.add(observer);
            }
        }

        void removeObserver(IObserver observer) {
            if (observer != null) {
                observers.remove(observer);
            }
        }

        protected void notifyObservers() {
            if (observers.size() < 1) {
                throw new RuntimeException("An ObservableElement invoked notifyObservers() method, yet no observers" +
                        " are found. Probably this instance was unregistered when delete() method was used. Do not" +
                        " use the instance that was deleted. Always retrieve the latest instance by get() method.");
            }
            observers.forEach(iObserver -> iObserver.update(this));
        }
    }
}
