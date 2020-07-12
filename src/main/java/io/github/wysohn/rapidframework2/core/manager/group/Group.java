package io.github.wysohn.rapidframework2.core.manager.group;

import io.github.wysohn.rapidframework2.core.interfaces.IPluginObject;
import io.github.wysohn.rapidframework2.core.interfaces.entity.IPermissionHolder;
import io.github.wysohn.rapidframework2.core.manager.caching.CachedElement;

import java.util.*;

public class Group extends CachedElement<UUID> implements IPermissionHolder, IPluginObject {
    private UUID ownerUuid;
    private UUID parentUuid;
    private String displayName;
    private String mark;

    private final Set<UUID> children = new HashSet<>();
    private final Map<String, Object> metaData = new HashMap<>();

    public Group(UUID key) {
        super(key);
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;

        notifyObservers();
    }

    public int childrenSize() {
        return children.size();
    }

    public boolean containsChild(UUID o) {
        return children.contains(o);
    }

    public boolean addChild(UUID uuid) {
        if (children.add(uuid)) {
            notifyObservers();

            return true;
        } else return false;
    }

    public boolean removeChild(UUID o) {
        if (children.remove(o)) {
            notifyObservers();

            return true;
        } else return false;
    }

    public void clearChildren() {
        children.clear();

        notifyObservers();
    }

    public Collection<UUID> getChildList(){
        return Collections.unmodifiableCollection(children);
    }

    public int metaSize() {
        return metaData.size();
    }

    public Object metaGet(String o) {
        return metaData.get(o);
    }

    public Object metaPut(String s, Object o) {
        Object put = metaData.put(s, o);
        notifyObservers();
        return put;
    }

    public Object metaRemove(String o) {
        Object remove = metaData.remove(o);
        notifyObservers();
        return remove;
    }

    public void metaClear() {
        metaData.clear();

        notifyObservers();
    }

    public void setParentUuid(UUID parentUuid) {
        this.parentUuid = parentUuid;

        notifyObservers();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.setStringKey(displayName);
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;

        notifyObservers();
    }

    @Override
    public UUID getParentUuid() {
        return parentUuid;
    }

    @Override
    public UUID getUuid() {
        return getKey();
    }
}
