package io.github.wysohn.rapidframework3.core.caching;

import com.google.inject.Injector;
import io.github.wysohn.rapidframework3.core.database.Database;
import io.github.wysohn.rapidframework3.core.database.Databases;
import io.github.wysohn.rapidframework3.core.database.migration.FieldToFieldMappingStep;
import io.github.wysohn.rapidframework3.core.database.migration.MigrationHelper;
import io.github.wysohn.rapidframework3.core.database.migration.MigrationSteps;
import io.github.wysohn.rapidframework3.core.main.Manager;
import io.github.wysohn.rapidframework3.core.main.ManagerConfig;
import io.github.wysohn.rapidframework3.core.paging.LRUCache;
import io.github.wysohn.rapidframework3.interfaces.caching.IObserver;
import io.github.wysohn.rapidframework3.interfaces.plugin.IShutdownHandle;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework3.interfaces.serialize.ITypeAsserter;
import io.github.wysohn.rapidframework3.utils.Validation;

import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class AbstractManagerElementCaching<K, V extends CachedElement<K>> extends Manager {
    private static final int DEFAULT_CACHE_SIZE = 1000;

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

    private final String pluginName;
    private final Logger logger;
    private final ManagerConfig config;
    private final File pluginDir;
    private final IShutdownHandle shutdownHandle;
    private final ISerializer serializer;
    private final Injector injector;
    private final Class<V> type;

    private Databases.DatabaseFactory<V> dbFactory;
    private Database<V> db;

    private final List<IObserver> observers = new ArrayList<IObserver>() {
        {
            add(new CachedElementObserver());
        }
    };

    private final Map<K, V> cachedElements;

    private final Map<String, K> nameToKeyMap = new HashMap<>();
    private final Map<K, String> keyToNameMap = new HashMap<>();

    /**
     *
     * @param pluginName
     * @param logger
     * @param config
     * @param pluginDir
     * @param shutdownHandle
     * @param serializer
     * @param asserter
     * @param injector
     * @param type not injectable. Must provided manually
     * @param cacheSize not injectable. Must provided manually
     */
    public AbstractManagerElementCaching(String pluginName,
                                         Logger logger,
                                         ManagerConfig config,
                                         File pluginDir,
                                         IShutdownHandle shutdownHandle,
                                         ISerializer serializer,
                                         ITypeAsserter asserter,
                                         Injector injector,
                                         Class<V> type,
                                         int cacheSize) {
        super();
        Validation.validate(cacheSize, v -> v > 0, "cache size must be > 0");
        this.cachedElements = new LRUCache<>(cacheSize);

        this.pluginName = pluginName;
        this.logger = logger;
        this.config = config;
        this.pluginDir = pluginDir;
        this.shutdownHandle = shutdownHandle;
        this.serializer = serializer;
        this.injector = injector;
        this.type = type;

        asserter.assertClass(type);
    }

    public AbstractManagerElementCaching(String pluginName,
                                         Logger logger,
                                         ManagerConfig config,
                                         File pluginDir,
                                         IShutdownHandle shutdownHandle,
                                         ISerializer serializer,
                                         ITypeAsserter asserter,
                                         Injector injector,
                                         Class<V> type) {
        this(pluginName,
             logger,
             config,
             pluginDir,
             shutdownHandle,
             serializer,
             asserter,
             injector,
             type,
             DEFAULT_CACHE_SIZE);
    }

    /**
     * Create DataFactory which will be used by this manager.
     *
     * @return
     */
    protected abstract Databases.DatabaseFactory<V> createDatabaseFactory();

    protected abstract K fromString(String string);

    protected abstract V newInstance(K key);

    @Override
    public void enable() throws Exception {
        dbFactory = createDatabaseFactory();

        // prevent any other works before initializing caches
        synchronized (cacheLock) {
            db = dbFactory.createDatabase(type,
                                          (String) config.get("dbType").orElse("file"),
                                          serializer);
            Validation.assertNotNull(db);

            Set<String> keys = db.getKeys();
            int i = Math.min(100, keys.size());
            for (String keyStr : keys) {
                if(--i < 0)
                    break;

                V value = db.load(keyStr);
                if (value == null)
                    continue;

                K key = fromString(keyStr);

                cache(key, value);
            }
        }
    }

    @Override
    public void load() throws Exception {

    }

    @Override
    public void disable() throws Exception {
        synchronized (cacheLock) {
            logger.info("Waiting for the save tasks to be done...");
            synchronized (dbLock){
                saveTaskPool.shutdown();
                saveTaskPool.awaitTermination(30, TimeUnit.SECONDS);  // wait for running tasks to finish
            }
            logger.info("Save finished.");
        }
    }

    protected MigrationHelper<K, V, V> migrateFrom(String dbType, MigrationSteps<K, V, V> steps) {
        Database<V> from = dbFactory.createDatabase(type, dbType, serializer);
        if (from == null)
            return null;

        if (from.getClass() == db.getClass())
            return null;

        return new MigrationHelper<>(logger,
                                     from,
                                     db,
                                     this::fromString,
                                     key -> getOrNew(key).map(Reference::get)
                                             .orElseThrow(RuntimeException::new),
                                     MigrationSteps.Builder.<K, V, V>begin()
                                             .step(new FieldToFieldMappingStep<>(type))
                                             .build());
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

        logger.severe("Key: " + key);
        logger.severe("Manager: " + getClass().getSimpleName());

        // At this point, irreversible data corruption can happen, so it's safer to turn off the plugin.
        shutdownHandle.shutdown();
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
     *
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
                synchronized (dbLock){
                    Validation.assertNotNull(db, "Key was " + key);
                    V loaded = db.load(key.toString());

                    if (loaded != null) {
                        AbstractManagerElementCaching.this.cache(key, loaded);
                    }
                }
            }  catch (Exception e) {
                handleDBOperationFailure(key.toString(), e);
            }

            value = cachedElements.get(key);
        }

        return Optional.ofNullable(value).map(WeakReference::new);
    }

    /**
     * Get value or create new instance provided by the supplier
     *
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

        return get(key);
    }

    private void cache(K key, V value) {
        injector.injectMembers(value);

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
     *
     * @param key associated key
     */
    public void delete(K key) {
        synchronized (cacheLock) {
            boolean result = deCache(key);

            saveTaskPool.submit(() -> {
                synchronized (dbLock){
                    try {
                        db.save(key.toString(), null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Remove the value associated 'key' but not from database.
     * This is useful if data in database has changed from different front-ends and
     * you want to reload the latest data from the database instead of using the cached data.
     * {@link #get(Object)} method will try to load data from the database if cache does not exist.
     *
     * @param key the key to clear the cache
     * @return true if cleared; false if it wasn't available anyway.
     */
    public boolean deCache(K key) {
        synchronized (cacheLock) {
            V original = cachedElements.remove(key);
            if (original != null) {
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

    public Set<String> stringKeySet(){
        synchronized (cacheLock) {
            return new HashSet<>(nameToKeyMap.keySet());
        }
    }

    /**
     * Work on each element. This is the snapshot copied at the moment, so it may not
     * accurately reflect every element when it is executed.
     *
     * @param consumer
     */
    public void forEach(Consumer<? super V> consumer, Consumer<Throwable> exConsumer) {
        keySet().stream()
                .map(this::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Reference::get)
                .forEach(v -> {
                    try {
                        consumer.accept(v);
                    } catch (Exception ex) {
                        exConsumer.accept(ex);
                    }
                });
    }

    /**
     * {@link #forEach(Consumer, Consumer)}
     *
     * @param consumer
     */
    public void forEach(Consumer<? super V> consumer) {
        this.forEach(consumer, ex -> logger.log(Level.FINE, "forEach()", ex));
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

    protected Databases.DatabaseFactory<V> getDatabaseFactory(String tablename) {
        return ((objType, dbType, others) -> {
            try {
                switch (dbType) {
                    case "mysql":
                        return Databases.build((String) config.get("db.address").orElse("127.0.0.1"),
                                (String) config.get("db.name").orElse(pluginName),
                                (String) config.get("db.tablename").orElse(tablename),
                                (String) config.get("db.username").orElse("root"),
                                (String) config.get("db.password").orElse("1234"));
                    default:
                        return Databases.build(tablename,
                                               pluginDir,
                                               (ISerializer) others[0],
                                               objType);
                }
            } catch (Exception e) {
                handleDBOperationFailure(tablename, e);
                return null;
            }
        });
    }

    public interface IConstructionHandle<K, V extends CachedElement<K>> {
        /**
         * Called after the object is created. It can be useful if some data has to be filled
         * manually after the object is instantiated.
         *
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
            // write-lock
            V value = (V) observable;

            synchronized (cacheLock) {
                // write-lock -> cache-lock
                cache(value.getKey(), value);

                saveTaskPool.submit(() -> {
                    // read-lock
                    V copied = value.copy();
                    // X
                    synchronized (dbLock){
                        // db-lock
                        try {
                            db.save(value.getKey().toString(), copied);
                        } catch (Exception e) {
                            handleDBOperationFailure(value.getKey().toString(), e);
                        }
                        // X
                    }
                });

                // write-lock
            }

            // X
        }
    }

    public abstract static class ObservableElement {
        private transient final List<IObserver> observers;
        private transient final ReentrantReadWriteLock lock;
        private transient final Constructor<? extends ObservableElement> con;

        public ObservableElement() {
            observers = new LinkedList<>();
            try {
                con = getClass().getDeclaredConstructor(getClass());
                con.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(getClass()+" does not implement copy constructor. Also, " +
                                                   "make sure it's not an inner-class.", e);
            }
            lock = new ReentrantReadWriteLock();
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

        /**
         * Copy current state. Internally, it just calls copy constructor.
         * This operation is thread-safe, therefore, it is free from any race conditions.
         * <p>
         * <p>
         * Previous implementation had significant flaw because when Gson (or any other
         * serialization framework) is reading
         * the object to serialize, the other thread still can access the object and
         * change the object's state, so this caused a race condition.
         * This method and {@link #notifyObservers()} now blocks each other,
         * so if the write operation (notifyObservers) is invoked while serialization is
         * still undergoing, it will be blocked until the read operation
         * (this method) is done, and vice versa.
         *
         * @param <T> whatever the type you are copying
         * @return the copied object
         */
        public <T extends ObservableElement> T copy() {
            if (con == null) {
                throw new RuntimeException("Copy constructor not initialized.");
            }

            lock.readLock().lock();
            try {
                return (T) con.newInstance(this);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                lock.readLock().unlock();
            }
        }

        /**
         * Warning) this call acquire the write-lock.
         */
        protected void notifyObservers() {
            lock.writeLock().lock();
            try {
                if (observers.size() < 1) {
                    throw new RuntimeException("An ObservableElement invoked notifyObservers() method, yet no observers" +
                            " are found. Probably this instance was unregistered when delete() method was used. Do not" +
                            " use the instance that was deleted. Always retrieve the latest instance by get() method.");
                }
                observers.forEach(iObserver -> iObserver.update(this));
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
}
