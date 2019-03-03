package io.github.wysohn.rapidframework.pluginbase.objects.permissions;

import java.util.UUID;

public interface PermissionHolderProvider {
    PermissionHolder getPermissionHolder(UUID uuid);
}
