package io.github.wysohn.rapidframework3.core.database.migration;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;
import io.github.wysohn.rapidframework3.core.database.IDatabase;
import io.github.wysohn.rapidframework3.utils.Validation;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;

public class MigrationHelper<K, FROM extends CachedElement<K>, TO extends CachedElement<K>> {
    private final Logger logger;
    private final IDatabase<K, FROM> fromDatabase;
    private final IDatabase<K, TO> toDatabase;
    private final Function<String, K> stringToKey;
    private final Function<K, TO> instanceSupplier;
    private final MigrationSteps<K, FROM, TO> steps;

    private ExecutorService currentPool;

    /**
     * Build a migration helper
     *
     * @param logger
     * @param fromDatabase
     * @param toDatabase
     * @param stringToKey
     * @param instanceSupplier this should get instance directly from
     *                         {@link io.github.wysohn.rapidframework3.core.caching.AbstractManagerElementCaching}
     * @param steps
     */
    public MigrationHelper(Logger logger,
                           IDatabase<K, FROM> fromDatabase,
                           IDatabase<K, TO> toDatabase,
                           Function<String, K> stringToKey,
                           Function<K, TO> instanceSupplier,
                           MigrationSteps<K, FROM, TO> steps) {
        this.logger = logger;
        this.fromDatabase = fromDatabase;
        this.toDatabase = toDatabase;
        this.stringToKey = stringToKey;
        this.instanceSupplier = instanceSupplier;
        this.steps = steps;
    }

    public boolean start() {
        if(currentPool != null && !currentPool.isTerminated())
            return false;

        int numThreads = 4;
        currentPool = Executors.newFixedThreadPool(numThreads);

        Queue<K> queue = new LinkedList<>(fromDatabase.getKeys());
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            currentPool.submit(new MigrationRunnable(queue, size));
        }
        return true;
    }

    public boolean stop(){
        if(currentPool == null || currentPool.isTerminated())
            return false;

        currentPool.shutdownNow();
        return true;
    }

    void waitForTermination(long time, TimeUnit unit){
        Optional.ofNullable(currentPool).ifPresent(pool -> {
            try {
                pool.awaitTermination(time, unit);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private class MigrationRunnable implements Runnable{
        private final Queue<K> keys;
        private final int size;

        private int processed = 0;

        public MigrationRunnable(Queue<K> keys, int size) {
            this.keys = keys;
            this.size = size;
        }

        @Override
        public void run() {
            while(true){
                K key;
                synchronized (keys){
                    key = keys.poll();
                }

                if(key == null)
                    return;

                try {
                    FROM from = fromDatabase.load(key);
                    TO to = instanceSupplier.apply(key);

                    Validation.validate(from, v -> Objects.equals(v.getKey(), key), "from key mismatch.");
                    Validation.validate(to, v -> Objects.equals(v.getKey(), key), "to key mismatch.");

                    steps.migrate(from, to);
                    toDatabase.save(key, to);

                    int now;
                    synchronized (keys) {
                        now = size - keys.size();
                        if(now > processed + 100){
                            processed = now;
                        } else {
                            continue;
                        }
                    }

                    logger.info(String.format("Migration %s -> %s", fromDatabase, toDatabase));
                    logger.info(String.format("%d / %d", now, size));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
