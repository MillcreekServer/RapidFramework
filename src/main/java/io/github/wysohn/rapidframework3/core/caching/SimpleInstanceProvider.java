package io.github.wysohn.rapidframework3.core.caching;

import io.github.wysohn.rapidframework3.core.interfaces.IPluginObject;
import io.github.wysohn.rapidframework3.core.interfaces.caching.IInstanceProvider;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleInstanceProvider<V extends IPluginObject> implements IInstanceProvider<V> {
    private final Class<V> clazz;
    private final AbstractManagerElementCaching<?, ?>[] managers;

    public SimpleInstanceProvider(Class<V> clazz, AbstractManagerElementCaching<?, ?>... managers) {
        this.clazz = clazz;
        this.managers = Arrays.stream(managers)
                .filter(Objects::nonNull)
                .distinct()
                .toArray(AbstractManagerElementCaching[]::new);
    }

    @Override
    public V get(UUID uuid) {
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
    public void forEachHolder(Consumer<V> consumer) {
        Stream.of(managers)
                .map(AbstractManagerElementCaching.class::cast)
                .forEach(manager -> manager.forEach(consumer));
    }

    @Override
    public List<V> search(Predicate<V> predicate) {
        return (List<V>) Stream.of(managers)
                .map(AbstractManagerElementCaching.class::cast)
                .map(manger -> manger.search(predicate))
                .flatMap(Collection::stream)
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }
}
