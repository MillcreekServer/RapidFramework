package io.github.wysohn.rapidframework.pluginbase.objects.permissions;

import java.util.UUID;

public interface PermissionHolder {
    /**
     * get unique id for this permission holder
     * 
     * @return
     */
    UUID getUuid();

    /**
     * get unique id of parent group
     * 
     * @return null if this group does not belong to any parent group; parent group
     *         otherwise
     */
    UUID getParentUuid();

    /**
     * set unique id of parent group
     * 
     * @param uuid the unique id of parent group; null to disconnect
     */
    void setParentUuid(UUID uuid);

}