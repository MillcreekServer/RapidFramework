package io.github.wysohn.rapidframework3.interfaces.language;

public interface DataProvider<T> {
    int size();

    T get(int index);

    default void sync(Runnable run) {
        run.run();
    }

    default boolean omit(T val) {
        return false;
    }
}