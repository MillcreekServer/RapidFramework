package io.github.wysohn.rapidframework3.utils;

public class Pair<K, V> {
    public final K key;
    public final V value;

    private Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }
}
