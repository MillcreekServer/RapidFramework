package io.github.wysohn.rapidframework2.core.manager.caching;

import java.util.UUID;
import java.util.function.Consumer;

public interface IInstanceProvider<T> {
    T get(UUID uuid);

    void forEachHolder(Consumer<T> consumer);
}
