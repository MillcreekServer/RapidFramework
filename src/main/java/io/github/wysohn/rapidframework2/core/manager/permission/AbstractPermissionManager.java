package io.github.wysohn.rapidframework2.core.manager.permission;

import io.github.wysohn.rapidframework2.core.database.Database;
import io.github.wysohn.rapidframework2.core.database.serialize.DefaultSerializer;
import io.github.wysohn.rapidframework2.core.interfaces.entity.IPermissionHolder;
import io.github.wysohn.rapidframework2.core.manager.caching.AbstractManagerElementCaching;
import util.Validation;

import java.util.Arrays;
import java.util.UUID;

public abstract class AbstractPermissionManager extends AbstractManagerElementCaching<UUID, PermissionStorage> {
    static {
        Database.registerTypeAdapter(PermissionStorage.class, new DefaultSerializer<PermissionStorage>());
    }

    private final IParentProvider parentProvider;

    public AbstractPermissionManager(int loadPriority, IParentProvider parentProvider) {
        super(loadPriority);
        Validation.assertNotNull(parentProvider);

        this.parentProvider = parentProvider;
    }

    @Override
    protected UUID fromString(String string) {
        return UUID.fromString(string);
    }

    /**
     * Check if this holder has permission. If this holder has parent (e.g. is member of group), then this method
     * will try to check all the permissions hierarchically. The check order would be most specific to general, which
     * means that try with the IPermissionHolder itself, check on its parent, check on parent's parent, and so on.
     *
     * @param holder      IPermissionHolder. If holder is null, return value is always false
     * @param permissions the permissions to check
     * @return true if has permission(s); false otherwise.
     */
    public boolean hasPermission(IPermissionHolder holder, IPermission... permissions) {
        if (holder == null)
            return false;

        PermissionStorage storage = getOrNew(holder.getUuid());

        if (Arrays.stream(permissions)
                .map(IPermission::getUuid)
                .anyMatch(storage::contains)) {
            return true;
        } else {
            return hasPermission(parentProvider.getHolder(main(), holder.getParentUuid()), permissions);
        }
    }

    /**
     * Add permission to the specified IPermissionHolder
     * @param holder the holder
     * @param permission permission to add
     * @return true if added; false if already has the permission
     */
    public boolean addPermission(IPermissionHolder holder, IPermission permission) {
        PermissionStorage storage = getOrNew(holder.getUuid());

        if (storage.contains(permission.getUuid()))
            return false;

        storage.add(permission.getUuid());

        return true;
    }

    /**
     * Remove permission from the specified IPermissionHolder
     * @param holder the holder
     * @param permission permission to remove
     * @return true if removed; false if didn't have it
     */
    public boolean removePermission(IPermissionHolder holder, IPermission permission){
        PermissionStorage storage = getOrNew(holder.getUuid());

        if(!storage.contains(permission.getUuid()))
            return false;

        storage.remove(permission.getUuid());

        return true;
    }

    /**
     * Delete all permission from the specified IPermissionHolder
     * @param holder the holder
     */
    public void resetPermission(IPermissionHolder holder){
        delete(holder.getUuid());
    }
}
