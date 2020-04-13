package io.github.wysohn.rapidframework2.core.manager.caching;

import io.github.wysohn.rapidframework2.core.interfaces.IPluginObject;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SimpleInstanceProvider<T extends IPluginObject> implements IInstanceProvider<T> {
    private final Class<T> clazz;
    private final AbstractManagerElementCaching<?, ?>[] managers;

    public SimpleInstanceProvider(Class<T> clazz, AbstractManagerElementCaching<?, ?>... managers) {
        this.clazz = clazz;
        this.managers = Arrays.stream(managers)
                .filter(Objects::nonNull)
                .distinct()
                .toArray(AbstractManagerElementCaching[]::new);
    }

    @Override
    public T get(UUID uuid) {
        return Stream.of(managers)
                .map(AbstractManagerElementCaching.class::cast)
                .map(manager -> manager.get(uuid))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(WeakReference.class::cast)
                .map(WeakReference::get)
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void forEachHolder(Consumer<T> consumer) {
        Stream.of(managers)
                .map(AbstractManagerElementCaching.class::cast)
                .forEach(manager -> manager.forEach(consumer));
    }
}
