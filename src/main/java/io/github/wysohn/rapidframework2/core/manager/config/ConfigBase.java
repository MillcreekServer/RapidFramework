package io.github.wysohn.rapidframework2.core.manager.config;

import io.github.wysohn.rapidframework2.core.interfaces.KeyValueStorage;

import java.lang.reflect.Field;

/**
 * <p>
 * Let automatically make public fields to be saved and loaded.
 * </p>
 * <p>
 * Child class only need to declare <b>public field with '_'</b> as _ will be
 * used to indicate the path. Fields with other than public modifier will be
 * ignored.
 *
 * <p>
 * For example) test_test field is equivalent to test.test in config
 * </p>
 *
 * @author wysohn
 */
abstract class ConfigBase {
    private static String convertToConfigName(String fieldName) {
        return fieldName.replaceAll("_", ".");
    }

    private static String converToFieldName(String configKey) {
        return configKey.replaceAll("\\.", "_");
    }

    /**
     * Write to storage with values assigned to the public fields of this class.
     *
     * @param storage tareget storage
     */
    protected void initEmptyFields(KeyValueStorage storage) {
        Field[] fields = this.getClass().getFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);

                String configName = convertToConfigName(field.getName());
                Object obj = field.get(this);

                if (storage.get(configName) == null && obj != null) {
                    storage.put(configName, obj);
                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Read data from stroage and assign it to the public fields of this class.
     *
     * @param storage
     */
    protected void readFromStorage(KeyValueStorage storage) {
        for (String key : storage.getKeys(true)) {
            if (key.contains("_COMMENT_"))
                continue;

            if (storage.isSection(key))
                continue;

            String fieldName = converToFieldName(key);

            try {
                Field field = this.getClass().getField(fieldName);
                field.setAccessible(true);

                field.set(this, storage.get(fieldName));
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Write public field values into the storage with converted public field names.
     *
     * @param storage
     */
    protected void writeToStorage(KeyValueStorage storage) {
        Field[] fields = this.getClass().getFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object obj = field.get(this);
                String configName = convertToConfigName(field.getName());

                storage.put(configName, obj);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
