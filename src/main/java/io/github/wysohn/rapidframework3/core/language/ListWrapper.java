package io.github.wysohn.rapidframework3.core.language;

import io.github.wysohn.rapidframework3.interfaces.language.DataProvider;

import java.util.List;

public class ListWrapper<T> implements DataProvider<T> {
    private final List<T> list;

    ListWrapper(List<T> list) {
        this.list = list;
    }

    public static <T> ListWrapper<T> wrap(List<T> list) {
        return new ListWrapper<>(list);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }
}
