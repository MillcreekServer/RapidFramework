package io.github.wysohn.rapidframework4.interfaces.permissin;

import io.github.wysohn.rapidframework4.interfaces.IPluginObject;

import java.util.UUID;

public interface IPermission extends IPluginObject {

    /**
     * This UUID should be unique and fixed representation of 'this' permission.
     * For example, do not use UUID.randomUUID() method to generate UUID for this permission
     * since it will change the UUID of 'this' permission every time.
     * <p>
     * However, if you want to keep the same information but want to create a 'new' permission, change the UUID to
     * something else, so 'this' and the 'new' permission has same internal information, yet they are actually
     * treated as different permission.
     *
     * @return the unique UUID correspond to this permission.
     */
    @Override
    UUID getUuid();
}
