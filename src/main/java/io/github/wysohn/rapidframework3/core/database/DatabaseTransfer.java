package io.github.wysohn.rapidframework3.core.database;

import java.util.Set;

public class DatabaseTransfer implements Runnable {
    private final ITransferCallback callback;
    private final TransferPair[] pairs;

    public DatabaseTransfer(ITransferCallback callback, TransferPair... pairs) {
        this.callback = callback;
        this.pairs = pairs;
    }

    @Override
    public void run() {
        callback.before();

        int pairi = 0;
        for (TransferPair pair : pairs) {
            Database from = pair.from;
            Database to = pair.to;
            Set<String> keys = from.getKeys();
            int i = 0, percentage = -1;
            for (String key : keys) {
                try {
                    String data = from.load(key);
                    to.save(key, data);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (getPercentage(i, keys.size()) % 5 == 0) {
                        callback.progress(getPercentage(i, keys.size()));
                    }
                    i++;
                }
            }
            callback.progress(100.0);
            pairi++;
        }

        System.gc();
        callback.after();
    }

    private int getPercentage(int cur, int outOf) {
        return (int) (((double) cur / outOf) * 100);
    }

    public interface ITransferCallback {
        void before();

        void progress(double percentage);

        void after();
    }

    public static class TransferPair {
        private final Database from;
        private final Database to;

        public TransferPair(Database from, Database to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return from.getClass().getSimpleName() + " ==> " + to.getClass().getSimpleName();
        }

    }

}
