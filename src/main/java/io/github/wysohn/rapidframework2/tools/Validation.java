package io.github.wysohn.rapidframework2.tools;

import java.util.function.Predicate;

public class Validation {
    public static void assertNotNull(Object obj, String message) {
        if (obj == null)
            throw new RuntimeException(message);
    }

    public static void assertNotNull(Object obj) {
        assertNotNull(obj, "Cannot be null here.");
    }

    public static <T> void validate(T val, Predicate<T> pred, String message) {
        if (pred.negate().test(val))
            throw new RuntimeException(message);
    }
}
