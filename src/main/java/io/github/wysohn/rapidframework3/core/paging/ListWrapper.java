package io.github.wysohn.rapidframework3.core.paging;

import io.github.wysohn.rapidframework3.interfaces.paging.DataProvider;

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
    public List<T> get(int index, int max) {
        return list.subList(index, Math.min(list.size(), index + max));
    }
}
