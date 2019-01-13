package io.github.wysohn.rapidframework.pluginbase.manager;

import io.github.wysohn.rapidframework.database.Database;
import io.github.wysohn.rapidframework.database.file.DatabaseFile;
import io.github.wysohn.rapidframework.database.mysql.DatabaseMysql;
import io.github.wysohn.rapidframework.database.tasks.DatabaseTransferTask.TransferPair;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginManager;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public abstract class ManagerElementCaching<K, V extends ManagerElementCaching.NamedElement>
        extends PluginManager<PluginBase> {
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

    private final Map<K, V> cachedElements = new HashMap<>();
    private final Map<String, K> nameMap = new HashMap<>();
    private Database<V> db;

    private CacheUpdateThread cacheUpdateThread;

    private boolean dbWriting = false;

    public ManagerElementCaching(PluginBase base, int loadPriority) {
        super(base, loadPriority);
    }

    @Override
    protected void onDisable() throws Exception {
        cacheUpdateThread.interrupt();
        saveTaskPool.shutdown();

        base.getLogger().info("Waiting for the save tasks to be done...");
        saveTaskPool.awaitTermination(10, TimeUnit.SECONDS);
        base.getLogger().info("Done!");
    }

    @Override
    protected void onEnable() throws Exception {
        try {
            if (base.getPluginConfig().MySql_Enabled) {
                db = createMysqlDB();
            }
        } catch (Exception e) {
            base.getLogger().warning(e.getMessage());
            base.getLogger().warning("Failed to initialize Mysql. file database. -- " + getClass().getSimpleName());
        } finally {
            if (db == null) {
                db = createFileDB();
            }
        }

        onReload();

        cacheUpdateThread = new CacheUpdateThread();
        cacheUpdateThread.start();
    }

    @Override
    protected void onReload() throws Exception {
        synchronized (cachedElements) {
            cachedElements.clear();
        }
        updateCache();
    }

    public void setCacheUpdaterStatus(boolean status) {
        synchronized(this.cacheUpdateThread) {
            this.cacheUpdateThread.enabled = status;
            if(status) {
                this.cacheUpdateThread.notifyAll();
            }
        }
    }

    public boolean getCacheUpdaterStatus() {
        return this.cacheUpdateThread.enabled;
    }

    public DatabaseFile<V> createFileDB() {
        return new DatabaseFile<V>(new File(base.getDataFolder(), getTableName()), getType());
    }

    public DatabaseMysql<V> createMysqlDB()
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        return new DatabaseMysql<V>(base.getPluginConfig().MySql_DBAddress, base.getPluginConfig().MySql_DBName,
                getTableName(), base.getPluginConfig().MySql_DBUser, base.getPluginConfig().MySql_DBPassword,
                getType());
    }

    /**
     *
     * @return Name of the table to save the data.
     */
    protected abstract String getTableName();

    /**
     *
     * @return The data type to be used when serializing/deserializing the data.
     */
    protected abstract Type getType();

    /**
     * Generate key from the given String.
     *
     * @param str
     *            the deserializable String value.
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

        synchronized (cachedElements) {
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
                if (before != null && before.getName() != null)
                    nameMap.remove(before.getName());
            }
        }

        for (K key : keys) {
            updateCache(key);
        }
    }

    private void updateCache(K key) {
        V newVal;

        synchronized (db) {
            newVal = db.load(key.toString(), null);
        }

        cache(key, newVal);
    }

    protected void cache(K key, V newVal) {
        cache(key, newVal, getUpdateHandle(), getDeleteHandle());
    }

    protected void cache(K key, V newVal, CacheUpdateHandle<K, V> updateHndle, CacheDeleteHandle<K, V> deleteHandle) {
        synchronized (cachedElements) {
            if (newVal == null) {
                V original = cachedElements.remove(key);

                if (deleteHandle != null) {
                    deleteHandle.onDelete(key, original);
                }

                if (original != null && original.getName() != null)
                    nameMap.remove(original.getName());
            } else {
                if (updateHndle != null) {
                    V out = updateHndle.onUpdate(key, newVal);
                    if (out != null)
                        newVal = out;
                }

                V original = cachedElements.put(key, newVal);

                if (newVal.getName() != null)
                    nameMap.put(newVal.getName(), key);

                if (original != null && original.getName() != null && !original.getName().equals(newVal.getName()))
                    nameMap.remove(original.getName());
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
     *
     * @param key
     * @param lock
     *            whether to wait for previous database operations or just use
     *            cache right away
     * @return
     */
    protected V get(K key, boolean lock) {
        synchronized (cachedElements) {
            if (lock) {
                synchronized (db) {
                    return cachedElements.get(key);
                }
            } else {
                return cachedElements.get(key);
            }
        }
    }

    /**
     *
     * @param name
     * @param lock
     *            whether to wait for previous database operations or just use
     *            cache right away
     * @return
     */
    protected V get(String name, boolean lock) {
        synchronized (cachedElements) {
            if (lock) {
                synchronized (db) {
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
     * Get copy of keys. This only represent snapshot of keys at the moment when this method is invoked.
     * @return snapshot of key set.
     */
    protected Set<K> getAllKeys() {
        synchronized (cachedElements) {
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
        synchronized (db) {
        }
    }

    protected void save(K key, V value) {
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
                synchronized (db) {
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

    protected void delete(final K key) {
        dbWriting = true;

        synchronized (cachedElements) {
            V original = cachedElements.get(key);
            if (original != null && original.getName() != null)
                nameMap.remove(original.getName());
        }

        saveTaskPool.submit(new Runnable() {
            @Override
            public void run() {
                synchronized (db) {
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
                while(!enabled) {
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

    public interface NamedElement {
        String getName();
    }

    public interface SaveHandle {
        /**
         * This method will be invoked before saving the value. It's not
         * asynchronous.
         */
        void preSave();

        /**
         * This method will be invoked after saving the value. It's
         * asynchronous.
         */
        void postSave();
    }

    public interface CacheUpdateHandle<K, T extends NamedElement> {
        /**
         * This method will be invoked every-time when a new information read
         * from database will be cached. This is good place to initialize
         * transient field values.
         *
         * @param key
         *            key used when load value from database
         * @param original
         *            original value. This can be null if the data was deleted
         *            from the database.
         * @return changed value. Return null will preserve the 'original' to be
         *         used as cache.
         */
        T onUpdate(K key, T original);
    }

    public interface CacheDeleteHandle<K, T extends NamedElement> {
        void onDelete(K key, T deleted);
    }
}
