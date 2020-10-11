package io.github.wysohn.rapidframework3.core.paging;

import java.util.LinkedHashMap;
import java.util.Map;

public class LFUCache<K, V> extends LinkedHashMap<K, V> {
    private final int cacheSize;

    public LFUCache(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() >= cacheSize;
    }
}
