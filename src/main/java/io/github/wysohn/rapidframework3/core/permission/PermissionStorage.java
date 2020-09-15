package io.github.wysohn.rapidframework3.core.permission;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PermissionStorage extends CachedElement<UUID> {
    private final Set<UUID> permissions;

    private PermissionStorage() {
        this(null);
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
        if (permissions.add(uuid)) {
            notifyObservers();

            return true;
        } else {
            return false;
        }
    }

    public boolean remove(UUID o) {
        if (permissions.remove(o)) {
            notifyObservers();

            return true;
        } else {
            return false;
        }
    }

    public void clear() {
        permissions.clear();

        notifyObservers();
    }
}
