package io.github.wysohn.rapidframework4.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CollectionHelper {
    @SafeVarargs
    public static <T> Set<T> set(T... values) {
        return new HashSet<>(Arrays.asList(values));
    }
}
