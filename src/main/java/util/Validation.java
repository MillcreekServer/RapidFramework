package util;

public class Validation {
    public static void assertNotNull(Object obj, String message) {
        if (obj == null)
            throw new RuntimeException(message);
    }

    public static void assertNotNull(Object obj) {
        assertNotNull(obj, "Cannot be null here.");
    }
}
