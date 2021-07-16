package io.github.wysohn.rapidframework3.core.group;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;
import io.github.wysohn.rapidframework3.interfaces.IPluginObject;
import io.github.wysohn.rapidframework3.interfaces.permissin.IPermissionHolder;

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
        mutate(() -> this.ownerUuid = ownerUuid);
    }

    public int childrenSize() {
        return children.size();
    }

    public boolean containsChild(UUID o) {
        return children.contains(o);
    }

    public boolean addChild(UUID uuid) {
        return mutate(() -> children.add(uuid));
    }

    public boolean removeChild(UUID o) {
        return mutate(() -> children.remove(o));
    }

    public void clearChildren() {
        mutate(children::clear);
    }

    public Collection<UUID> getChildList() {
        return Collections.unmodifiableCollection(children);
    }

    public int metaSize() {
        return metaData.size();
    }

    public Object metaGet(String o) {
        return metaData.get(o);
    }

    public Object metaPut(String s, Object o) {
        return mutate(() ->  metaData.put(s, o));
    }

    public Object metaRemove(String o) {
        return mutate(() -> metaData.remove(o));
    }

    public void metaClear() {
        mutate(metaData::clear);
    }

    public void setParentUuid(UUID parentUuid) {
        mutate(() -> this.parentUuid = parentUuid);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        mutate(() -> {
            this.displayName = displayName;
            this.setStringKey(displayName);
        });
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        mutate(() -> this.mark = mark);
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
