package io.github.wysohn.rapidframework4.core.paging;

import io.github.wysohn.rapidframework4.interfaces.paging.DataProvider;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataProviderProxy<T> implements DataProvider<T> {
    private final Map<Range, Page<T>> pages = new LRUCache<>(16);
    private final Function<Range, List<T>> updater;
    private final Supplier<Integer> sizeSupplier;
    private final long QUERY_DELAY;

    // no locking by default
    private Consumer<Runnable> readlock = Runnable::run;

    public DataProviderProxy(Function<Range, List<T>> updater, Supplier<Integer> sizeSupplier, long queryDelay) {
        QUERY_DELAY = queryDelay;
        this.updater = updater;
        this.sizeSupplier = sizeSupplier;
    }

    public DataProviderProxy(Function<Range, List<T>> updater, Supplier<Integer> sizeSupplier) {
        this(updater, sizeSupplier, 1000L);
    }

    public DataProviderProxy(List<T> original, long queryDelay) {
        this(range -> original.subList(range.index, Math.min(original.size(), range.index + range.size)),
                original::size,
                queryDelay);
    }

    public DataProviderProxy<T> attachReadLock(Consumer<Runnable> readlock){
        this.readlock = readlock;
        return this;
    }

    private List<T> getOrUpdate(Range range) {
        Page<T> page = pages.computeIfAbsent(range, Page::new);

        if (System.currentTimeMillis() < page.getLastUpdate() + QUERY_DELAY)
            return page.getList();

        page.setLastUpdate(System.currentTimeMillis());
        readlock.accept(() -> page.setList(updater.apply(range)));
        return page.getList();
    }

    @Override
    public int size() {
        return sizeSupplier.get();
    }

    @Override
    public List<T> get(int index, int size) {
        return getOrUpdate(Range.of(index, size));
    }
}
