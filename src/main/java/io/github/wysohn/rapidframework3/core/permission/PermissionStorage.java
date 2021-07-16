package io.github.wysohn.rapidframework3.core.permission;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PermissionStorage extends CachedElement<UUID> {
    private final Set<UUID> permissions;

    private PermissionStorage() {
        super((UUID) null);
        permissions = new HashSet<>();
    }

    private PermissionStorage(PermissionStorage copy){
        super(copy.getKey());
        permissions = new HashSet<>();
        permissions.addAll(copy.permissions);
    }

    public PermissionStorage(UUID key) {
        super(key);
        permissions = new HashSet<>();
    }

    public int size() {
        return permissions.size();
    }

    public boolean contains(UUID o) {
        return permissions.contains(o);
    }

    public boolean add(UUID uuid) {
        return mutate(() -> permissions.add(uuid));
    }

    public boolean remove(UUID o) {
        return mutate(() -> permissions.remove(o));
    }

    public void clear() {
        mutate(permissions::clear);
    }
}
