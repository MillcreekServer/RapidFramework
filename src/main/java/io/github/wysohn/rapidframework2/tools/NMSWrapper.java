package io.github.wysohn.rapidframework2.tools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NMSWrapper {
    private Class<?> clazz;
    private Object target;

    private Object result = null;

    private NMSWrapper(Class<?> target) {
        this.clazz = target;
        this.target = null;
    }

    private NMSWrapper(Object target) {
        this.clazz = target.getClass();
        this.target = target;
    }

    public static NMSWrapper target(Object target) {
        return new NMSWrapper(target);
    }

    public static NMSWrapper target(Class<?> target) {
        return new NMSWrapper(target);
    }

    /**
     * Invoke the method of the wrapper's current target. Return value can be retrieved
     * by {@link #result()} method. Or, you may choose to make a chained calls this method again to
     * reuse the result of the previous call's result as target of next invocation.
     * <p>
     * NPE is possible if the method is instance method, yet target instance is not provided.
     * Refer to {@link Method#invoke(Object, Object...)} for details
     *
     * @param methodName
     * @param args
     * @return
     */
    public NMSWrapper invoke(String methodName, Object... args) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        // replace last result for current target
        if (result != null) {
            target = result;
            clazz = result.getClass();
            result = null;
        }

        Method method = clazz.getDeclaredMethod(methodName, toClasses(args));
        method.setAccessible(true);

        result = method.invoke(target, args);

        return this;
    }

    private Class<?>[] toClasses(Object[] args) {
        return Arrays.stream(args)
                .map(Object::getClass)
                .map(c -> {
                    return primitiveMappings.getOrDefault(c, c);
                })
                .toArray(Class<?>[]::new);
    }

    /**
     * get result of the last invocation.
     *
     * @param <T>
     * @return
     */
    public <T> Optional<T> result() {
        return Optional.ofNullable((T) result);
    }

    /**
     * get field value of the wrapper's current target. NPE is possible when the field is instance field,
     * yet the target instance is not provided. Refer to {@link Field#get(Object)} for details.
     *
     * @param fieldName
     * @return
     */
    public Object get(String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    private static final Map<Class<?>, Class<?>> primitiveMappings = new HashMap<>();

    static {
        primitiveMappings.put(Boolean.class, boolean.class);
        primitiveMappings.put(Character.class, char.class);
        primitiveMappings.put(Byte.class, byte.class);
        primitiveMappings.put(Short.class, short.class);
        primitiveMappings.put(Integer.class, int.class);
        primitiveMappings.put(Long.class, long.class);
        primitiveMappings.put(Float.class, float.class);
        primitiveMappings.put(Double.class, double.class);
        primitiveMappings.put(Void.class, void.class);
    }
}
