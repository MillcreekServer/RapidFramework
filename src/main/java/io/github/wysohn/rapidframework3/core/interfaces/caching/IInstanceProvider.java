package io.github.wysohn.rapidframework3.core.interfaces.caching;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface IInstanceProvider<V> {
    V get(UUID uuid);

    void forEachHolder(Consumer<V> consumer);

    List<V> search(Predicate<V> predicate);
}
