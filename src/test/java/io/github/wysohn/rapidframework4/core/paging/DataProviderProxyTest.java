package io.github.wysohn.rapidframework4.core.paging;

import io.github.wysohn.rapidframework4.interfaces.paging.DataProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DataProviderProxyTest {

    private List<Integer> list;

    @Before
    public void init() {
        list = new ArrayList<>();
        for (int i = 0; i < 33; i++)
            list.add(i);
    }

    @Test
    public void size() {
        DataProvider<Integer> dataProvider = new DataProviderProxy<>(range ->
                list.subList(range.index, Math.min(list.size(), range.index + range.size)),
                                                                     list::size);

        assertEquals(33, dataProvider.size());
    }

    @Test
    public void get() {
        DataProvider<Integer> dataProvider = new DataProviderProxy<>(range ->
                list.subList(range.index, Math.min(list.size(), range.index + range.size)),
                list::size);

        assertEquals(list.subList(0, 10), dataProvider.get(0, 10));
        assertEquals(list.subList(10, 20), dataProvider.get(10, 10));
        assertEquals(list.subList(20, 30), dataProvider.get(20, 10));
        assertEquals(list.subList(30, 33), dataProvider.get(30, 10));

        assertEquals(list.subList(0, 10), dataProvider.get(0, 10));
        assertEquals(list.subList(10, 20), dataProvider.get(10, 10));
        assertEquals(list.subList(20, 30), dataProvider.get(20, 10));
        assertEquals(list.subList(30, 33), dataProvider.get(30, 10));
    }
}