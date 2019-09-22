package io.github.wysohn.rapidframework2.core.interfaces.entity;

public interface IPermissionHolder {
    /**
     * Check if this holder has permission.
     *
     * @param type       check type. If AND, then all of the given permission must exist, and if OR, then at least
     *                   one of the given permission must exist.
     * @param permission the permissions to check
     * @return true if has permission(s); false otherwise.
     */
    boolean hasPermission(CheckType type, String... permission);

    enum CheckType {
        AND, OR;
    }
}
