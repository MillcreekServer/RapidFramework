package io.github.wysohn.rapidframework4.interfaces.paging;

import java.util.List;

public interface DataProvider<T> {
    /**
     * Get total number of data
     *
     * @return
     */
    int size();

    /**
     * Get range of data [index ~ index + size)
     *
     * @param index starting index
     * @param size  size
     * @return the data in range. It may contains null.
     */
    List<T> get(int index, int size);

    default void sync(Runnable run) {
        run.run();
    }

    default boolean omit(T val) {
        return false;
    }
}