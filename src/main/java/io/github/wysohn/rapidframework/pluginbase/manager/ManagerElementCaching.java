package io.github.wysohn.rapidframework.pluginbase.manager;

import io.github.wysohn.rapidframework.database.Database;
import io.github.wysohn.rapidframework.database.Database.DatabaseFactory;
import io.github.wysohn.rapidframework.database.file.DatabaseFile;
import io.github.wysohn.rapidframework.database.mysql.DatabaseMysql;
import io.github.wysohn.rapidframework.database.tasks.DatabaseTransferTask.TransferPair;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginManager;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public abstract class ManagerElementCaching<PB extends PluginBase, K, V extends ManagerElementCaching.NamedElement>
        extends PluginManager<PB> {
    /*
     * Lock ordering should be cachedElements -> db if it has to be nested.
     */

    private final ExecutorService saveTaskPool = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        }
    });

    private final Object cacheLock = new Object();
    private final Object dbLock = new Object();

    private final Map<K, V> cachedElements = new HashMap<>();
    private final Map<String, K> nameMap = new HashMap<>();
    private final DatabaseFactory<V> dbFactory;

    private Database<V> db;

    private boolean useNameMap = false;
    private boolean dbWriting = false;

    public ManagerElementCaching(PB base, int loadPriority, DatabaseFactory<V> dbFactory) {
        super(base, loadPriority);
        this.dbFactory = dbFactory;
    }

    protected static <V> DatabaseFactory<V> createDatabaseFactory(PluginBase base, String tableName, Class<V> type) {
        return () -> {
            Database<V> db = null;

            try {
                if (base.getPluginConfig().MySql_Enabled) {
                    db = new DatabaseMysql<V>(type, base.getPluginConfig().MySql_DBAddress,
                            base.getPluginConfig().MySql_DBName, tableName, base.getPluginConfig().MySql_DBUser,
                            base.getPluginConfig().MySql_DBPassword);
                }
            } catch (Exception e) {
                base.getLogger().warning(e.getMessage());
                base.getLogger().warning("Failed to initialize Mysql. falling back to file database.");
            } finally {
                if (db == null) {
                    db = new DatabaseFile<V>(type, new File(base.getDataFolder(), tableName));
                }
            }

            return db;
        };
    }

    public void setUseNameMap(boolean useNameMap) {
        this.useNameMap = useNameMap;
    }

    @Override
    protected void onDisable() throws Exception {
        saveTaskPool.shutdown();

        base.getLogger().info("Waiting for the save tasks to be done...");
        saveTaskPool.awaitTermination(10, TimeUnit.SECONDS);
        base.getLogger().info("Done!");
    }

    @Override
    protected void onEnable() throws Exception {
        onReload();
    }

    @Override
    protected void onReload() throws Exception {
        synchronized (dbLock) {
            db = dbFactory.getDatabase();
        }

        synchronized (cacheLock) {
            cachedElements.clear();
        }
        updateCache();
    }

    @Override
    protected Map<String, Object> getInfo() {
        Map<String, Object> map = new HashMap<>();

        Map<String, String> databaseMap = new HashMap<>();
        if (db instanceof DatabaseMysql) {
            databaseMap.put("Type", "MySQL");
            databaseMap.put("Host", base.getPluginConfig().MySql_DBAddress);
        } else {
            databaseMap.put("Type", "FlatFile");
        }
        databaseMap.put("Name", db.getTableName());
        map.put("Database", databaseMap);

        return map;
    }

    /**
     * Generate key from the given String.
     *
     * @param str the deserializable String value.
     * @return deserialized object.
     */
    protected abstract K createKeyFromString(String str);

    /**
     * Get update gamehandle to be used caching the data.
     *
     * @return
     */
    protected abstract CacheUpdateHandle<K, V> getUpdateHandle();

    /**
     * Get delete gamehandle to be used removing data from cache.
     *
     * @return
     */
    protected abstract CacheDeleteHandle<K, V> getDeleteHandle();

    public TransferPair<V> getTransferPair(Database<V> from) {
        return new TransferPair<V>(from, db);
    }

    private void updateCache() {
        Set<String> strKeys = db.getKeys();

        Set<K> keys = new HashSet<>();
        Set<K> remove = new HashSet<>();

        synchronized (cacheLock) {
            for (K key : cachedElements.keySet()) {
                if (!strKeys.contains(key.toString()))
                    remove.add(key);
            }
            for (String strKey : strKeys) {
                K key = null;

                try {
                    key = createKeyFromString(strKey);
                } catch (Exception e) {
                    base.getLogger().warning("Removing invalid item with wrong key.");
                    base.getLogger().warning("Key [" + strKey + "] is not an UUID.");
                    base.getLogger().warning("In " + getClass().getSimpleName());
                } finally {
                    if (key != null)
                        keys.add(key);
                }
            }

            for (K key : remove) {
                V before = cachedElements.remove(key);
                if (before != null && before.getDisplayName() != null)
                    nameMap.remove(before.getDisplayName());
            }
        }

        for (K key : keys) {
            updateCache(key);
        }
    }

    private void updateCache(K key) {
        V newVal;

        synchronized (dbLock) {
            newVal = db.load(key.toString(), null);
        }

        cache(key, newVal);
    }

    protected void cache(K key, V newVal) {
        cache(key, newVal, getUpdateHandle(), getDeleteHandle());
    }

    protected void cache(K key, V newVal, CacheUpdateHandle<K, V> updateHndle, CacheDeleteHandle<K, V> deleteHandle) {
        synchronized (cacheLock) {
            if (newVal == null) {
                V original = cachedElements.remove(key);

                if (deleteHandle != null) {
                    deleteHandle.onDelete(key, original);
                }

                if (original != null && original.getDisplayName() != null)
                    nameMap.remove(original.getDisplayName());
            } else {
                if (updateHndle != null) {
                    V out = updateHndle.onUpdate(key, newVal);
                    if (out != null)
                        newVal = out;
                }

                V original = cachedElements.put(key, newVal);

                if (newVal.getDisplayName() != null)
                    nameMap.put(newVal.getDisplayName(), key);

                if (original != null && original.getDisplayName() != null && !original.getDisplayName().equals(newVal.getDisplayName()))
                    nameMap.remove(original.getDisplayName());
            }
        }
    }

    public V get(K key) {
        return get(key, false);
    }

    public V get(String name) {
        return get(name, false);
    }

    /**
     * @param key
     * @param lock whether to wait for previous database operations or just use
     *             cache right away
     * @return
     */
    protected V get(K key, boolean lock) {
        synchronized (cacheLock) {
            if (lock) {
                synchronized (dbLock) {
                    return cachedElements.get(key);
                }
            } else {
                return cachedElements.get(key);
            }
        }
    }

    /**
     * @param name
     * @param lock whether to wait for previous database operations or just use
     *             cache right away
     * @return
     */
    protected V get(String name, boolean lock) {
        synchronized (cacheLock) {
            if (lock) {
                synchronized (dbLock) {
                    K key = nameMap.get(name);
                    if (key == null)
                        return null;
                    else
                        return cachedElements.get(key);
                }
            } else {
                K key = nameMap.get(name);
                if (key == null)
                    return null;
                else
                    return cachedElements.get(key);
            }
        }
    }

    /**
     * Get copy of keys. This only represent snapshot of keys at the moment when
     * this method is invoked.
     *
     * @return snapshot of key set.
     */
    public Set<K> getAllKeys() {
        synchronized (cacheLock) {
            Set<K> newSet = new HashSet<>();
            newSet.addAll(cachedElements.keySet());
            return newSet;
        }
    }

    /**
     * Lock thread until previous database works are done
     *
     * @param key
     */
    public void check() {
        synchronized (dbLock) {
        }
    }

    public void save(K key, V value) {
        save(key, value, null);
    }

    protected void save(final K key, final V value, final SaveHandle handle) {
        dbWriting = true;

        if (handle != null) {
            try {
                handle.preSave();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        cache(key, value);

        saveTaskPool.submit(new Runnable() {
            @Override
            public void run() {
                synchronized (dbLock) {
                    try {
                        db.save(key.toString(), value);

                        if (handle != null) {
                            handle.postSave();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        dbWriting = false;
                    }
                }
            }
        });
    }

    public void delete(final K key) {
        dbWriting = true;

        synchronized (cacheLock) {
            V original = cachedElements.get(key);
            if (original != null && original.getDisplayName() != null)
                nameMap.remove(original.getDisplayName());
        }

        saveTaskPool.submit(new Runnable() {
            @Override
            public void run() {
                synchronized (dbLock) {
                    try {
                        db.save(key.toString(), null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        dbWriting = false;
                    }
                }
            }
        });
    }

    protected void deleteNameKey(String key) {
        nameMap.remove(key);
    }

    public K getKey(String name) {
        return nameMap.get(name);
    }

    public interface NamedElement {
        String getDisplayName();
    }

    public interface SaveHandle {
        /**
         * This method will be invoked before saving the value. It's not asynchronous.
         */
        void preSave();

        /**
         * This method will be invoked after saving the value. It's asynchronous.
         */
        void postSave();
    }

    public interface CacheUpdateHandle<K, T extends NamedElement> {
        /**
         * This method will be invoked every-time when a new information read from
         * database will be cached. This is good place to initialize transient field
         * values.
         *
         * @param key      key used when load value from database
         * @param original original value. This can be null if the data was deleted from
         *                 the database.
         * @return changed value. Return null will preserve the 'original' to be used as
         * cache.
         */
        T onUpdate(K key, T original);
    }

    public interface CacheDeleteHandle<K, T extends NamedElement> {
        void onDelete(K key, T deleted);
    }

    private class CacheUpdateThread extends Thread {
        boolean enabled = false;

        CacheUpdateThread() {
            this.setPriority(MIN_PRIORITY);
            this.setName("CacheUpdateThread");
            this.setDaemon(true);
        }

        @Override
        public void run() {
            while (!Thread.interrupted() && base.isEnabled()) {
                while (!enabled) {
                    try {
                        synchronized (this) {
                            wait();
                        }
                    } catch (InterruptedException e1) {
                    }
                }

                try {
                    if (!dbWriting)
                        updateCache();
                } catch (Exception e) {
                    e.printStackTrace();
                    base.getLogger().severe("CacheUpdateThread had to be stopped!");
                    base.getLogger().severe("Data will be out of sync if you have another server using the plugin!");
                    base.getLogger().severe("It's better to restart the server before any data corruption happens.");
                }

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                }
            }

        }
    }
}
