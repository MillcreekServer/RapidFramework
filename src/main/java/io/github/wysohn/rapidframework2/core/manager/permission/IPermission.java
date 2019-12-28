package io.github.wysohn.rapidframework2.core.manager.permission;

import io.github.wysohn.rapidframework2.core.interfaces.entity.IPluginEntity;
import io.github.wysohn.rapidframework2.core.manager.lang.DynamicLang;

import java.util.UUID;
import java.util.function.Predicate;

public interface IPermission extends IPluginEntity {
    DynamicLang getName();

    DynamicLang getDescription();

    /**
     * This UUID should be unique and fixed representation of 'this' permission.
     * For example, do not use UUID.randomUUID() method to generate UUID for this permission
     * since it will change the UUID of 'this' permission every time.
     *
     * However, if you want to keep the same information but want to create a 'new' permission, change the UUID to
     * something else, so 'this' and the 'new' permission has same internal information, yet they are actually
     * treated as different permission.
     * @return the unique UUID correspond to this permission.
     */
    @Override
    UUID getUuid();
}
