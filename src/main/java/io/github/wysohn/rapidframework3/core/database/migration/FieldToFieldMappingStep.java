package io.github.wysohn.rapidframework3.core.database.migration;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

public class FieldToFieldMappingStep<K, V extends CachedElement<K>> implements IMigrationStep<V, V>{
    private static final Predicate<Integer> IS_STATIC = (modifier) ->
            (modifier & Modifier.STATIC) == Modifier.STATIC;
    private static final Predicate<Integer> IS_TRANSIENT = (modifier) ->
            (modifier & Modifier.TRANSIENT) == Modifier.TRANSIENT;

    private final Class<V> clazz;
    private final Predicate<Integer> deniedModifier;

    public FieldToFieldMappingStep(Class<V> clazz, Predicate<Integer> deniedModifier) {
        this.clazz = clazz;
        this.deniedModifier = deniedModifier;
    }

    /**
     * By default, transient or static fields will be ignored
     * @param clazz
     */
    public FieldToFieldMappingStep(Class<V> clazz) {
        this(clazz, IS_TRANSIENT.or(IS_STATIC));
    }

    @Override
    public void migrate(V from, V to) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if(!deniedModifier.test(field.getModifiers()))
                continue;

            field.setAccessible(true);

            try {
                Object value = field.get(from);
                field.set(to, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
