package io.github.wysohn.rapidframework.utils;

import java.util.function.Predicate;

public class Validation {
    /**
     * Null check with default message
     * 
     * @param obj
     */
    public static <T> void validate(T obj) {
	validate(obj, x -> x == null, "Cannot be null");
    }

    /**
     * Null check with custom message
     * 
     * @param obj
     * @param message
     */
    public static <T> void validate(T obj, String message) {
	validate(obj, x -> x == null, message);
    }

    /**
     * Custom test with custom message
     * 
     * @param obj
     * @param pred    the fail condition
     * @param message
     */
    public static <T> void validate(T obj, Predicate<T> pred, String message) {
	if (pred.test(obj))
	    throw new RuntimeException(message);
    }
}
