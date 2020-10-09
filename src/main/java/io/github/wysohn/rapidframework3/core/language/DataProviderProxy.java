package io.github.wysohn.rapidframework3.core.language;

import io.github.wysohn.rapidframework3.interfaces.language.DataProvider;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class DataProviderProxy<T> implements DataProvider<T> {
    private final Supplier<List<T>> updater;
    private final long QUERY_DELAY;

    private Comparator<T> comparator;
    private DataProvider<T> cache;
    private long lastQuery = -1L;

    public DataProviderProxy(Supplier<List<T>> updater, long queryDelay) {
        QUERY_DELAY = queryDelay;
        this.updater = updater;
    }

    public DataProviderProxy(Supplier<List<T>> updater) {
        this(updater, 1000L);
    }

    /**
     * Make this provider to sort the list when updating the list to the latest version.
     * Setting it to null will disable sorting.
     *
     * @param comparator comparator to be used to sort the latest data; null to disable
     * @return this instance for builder pattern
     */
    public DataProviderProxy<T> sortOnUpdate(Comparator<T> comparator) {
        this.comparator = comparator;
        return this;
    }

    private void update() {
        if (System.currentTimeMillis() < lastQuery + QUERY_DELAY)
            return;
        lastQuery = System.currentTimeMillis();

        List<T> copy = updater.get();
        if (comparator != null)
            copy.sort(comparator);
        cache = new DataProvider<T>() {
            @Override
            public int size() {
                return copy.size();
            }

            @Override
            public T get(int i) {
                return copy.get(i);
            }
        };
    }

    @Override
    public int size() {
        update();
        return cache.size();
    }

    @Override
    public T get(int index) {
        update();
        return cache.get(index);
    }
}
