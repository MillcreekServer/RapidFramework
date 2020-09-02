package io.github.wysohn.rapidframework2.tools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class NMSWrapper {
    private Class<?> clazz;
    private Object target;
    private Method method;

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
     * Prepare method to be invoked.
     *
     * @param methodName
     * @param types
     * @return
     * @throws NoSuchMethodException
     */
    public NMSWrapper prepare(String methodName, Class<?>... types) throws NoSuchMethodException {
        // replace last result for current target
        if (result != null) {
            target = result;
            clazz = result.getClass();
            result = null;
        }

        method = clazz.getDeclaredMethod(methodName, types);
        method.setAccessible(true);

        return this;
    }

    /**
     * *Must invoke {@link #prepare(String, Class[])} to prepare the method to be invoked.
     * <p>
     * Invoke the method of the wrapper's current target. Return value can be retrieved
     * by {@link #result()} method. Or, you may choose to make a chained calls this method again to
     * reuse the result of the previous call's result as target of next invocation.
     * <p>
     * NPE is possible if the method is instance method, yet target instance is not provided.
     * Refer to {@link Method#invoke(Object, Object...)} for details
     *
     * @param args
     * @return
     */
    public NMSWrapper invoke(Object... args) throws InvocationTargetException, IllegalAccessException {
        result = method.invoke(target, args);
        method = null;

        return this;
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
}
