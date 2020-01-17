package io.github.wysohn.rapidframework2.core.manager.permission;

import io.github.wysohn.rapidframework2.core.manager.caching.CachedElement;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PermissionStorage extends CachedElement<UUID>{
    private final Set<UUID> permissions;

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
        if(permissions.add(uuid)){
            setChanged();
            notifyObservers();

            return true;
        }else{
            return false;
        }
    }

    public boolean remove(UUID o) {
        if(permissions.remove(o)){
            setChanged();
            notifyObservers();

            return true;
        }else{
            return false;
        }
    }

    public void clear() {
        permissions.clear();

        setChanged();
        notifyObservers();
    }
}
