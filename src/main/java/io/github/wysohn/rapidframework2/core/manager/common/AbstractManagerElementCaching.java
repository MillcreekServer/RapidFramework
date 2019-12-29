package io.github.wysohn.rapidframework2.core.manager.common;

import io.github.wysohn.rapidframework.utils.files.FileUtil;
import io.github.wysohn.rapidframework2.core.database.Database;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.manager.NamedElement;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import util.Validation;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class AbstractManagerElementCaching<K, V extends NamedElement> extends PluginMain.Manager {
    private final ExecutorService saveTaskPool = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setPriority(Thread.MIN_PRIORITY);
        return thread;
    });

    /*
     * Lock ordering should be cacheLock -> dbLock if it has to be nested.
     */
    private final Object cacheLock = new Object();
    private final Object dbLock = new Object();
    private final Database.DatabaseFactory<V> dbFactory;

    private Database<V> db;

    private final Map<K, V> cachedElements = new HashMap<>();
    private final Map<String, K> nameMap = new HashMap<>();

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

        synchronized (dbLock){
            for(String keyStr : db.getKeys()){
                V value = db.load(keyStr, null);
                if (value != null){
                    K key = fromString(keyStr);

                    cachedElements.put(key, value);
                    if (value.getName() != null)
                        nameMap.put(value.getName(), key);
                }
            }
        }
    }

    @Override
    public void disable() throws Exception {
        saveTaskPool.shutdown();

        main().getLogger().info("Waiting for the save tasks to be done...");
        saveTaskPool.awaitTermination(10, TimeUnit.SECONDS);
        main().getLogger().info("Save finished.");
    }

    public Optional<V> get(String name){
        synchronized (cacheLock){
            return get(nameMap.get(name));
        }
    }

    public Optional<V> get(K key){
        if(key == null)
            return Optional.empty();

        synchronized (cacheLock){
            synchronized (dbLock){
                if(cachedElements.containsKey(key))
                    return Optional.of(cachedElements.get(key));
                else
                    return Optional.empty();
            }
        }
    }

    /**
     * Save new value or replace the existing value. 'null' value will delete the entry completely.
     * @param key associated key
     * @param value value to be saved
     * @return true if saved; false if the 'name' (NamedValue) already exist.
     */
    public boolean save(K key, V value) {
        synchronized (cacheLock) {
            if (value == null) {
                V prev = cachedElements.remove(key);
                if (prev != null && prev.getName() != null)
                    nameMap.remove(prev.getName());
            } else {
                if(value.getName() != null && nameMap.containsKey(value.getName()))
                    return false;

                cachedElements.put(key, value);
                nameMap.put(value.getName(), key);
            }

            saveTaskPool.submit(() -> {
                synchronized (dbLock) {
                    db.save(key.toString(), value);
                }
            });

            return true;
        }
    }

    public void delete(K key) {
        synchronized (cacheLock) {
            V original = cachedElements.remove(key);
            if (original != null && original.getName() != null) {
                nameMap.remove(original.getName());
            }

            saveTaskPool.submit(() -> {
                synchronized (dbLock){
                    db.save(key.toString(), null);
                }
            });
        }
    }

    public Set<K> keySet(){
        return new HashSet<>(cachedElements.keySet());
    }

    protected <T> Database.DatabaseFactory<T> getCivGroupDatabaseFactory(Class<T> clazz, String tablename) {
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
}
