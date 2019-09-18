package io.github.wysohn.rapidframework.pluginbase.objects.permissions;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class PermissionHolder {
    private Set<Permission> permissions = new HashSet<>();

    /**
     * Get parent's UUID of this PermissionHolder.
     * @return the UUID of parent; null if no parent is set
     */
    public abstract UUID getParentUuid();

    protected abstract void setParentUuid(UUID parentUuid);

    /**
     * Check if the 'permission' exist for this PermissionHolder
     * @param permission permission to check
     * @return true if has it; false otherwise
     */
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    /**
     * Add permission to this PermissionHolder.
     * Nothing happens if already added.
     * @param permission permission to add.
     */
    void setPermission(Permission permission) {
        permissions.add(permission);
    }

    /**
     * Remove permission from this PermissionHolder.
     * Nothing happens if the permission doesn't exist.
     *
     * @param permission permission to delete.
     */
    void removePermission(Permission permission) {
        permissions.add(permission);
    }
}