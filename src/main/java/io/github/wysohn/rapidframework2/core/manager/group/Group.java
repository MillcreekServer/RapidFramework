package io.github.wysohn.rapidframework2.core.manager.group;

import io.github.wysohn.rapidframework2.core.interfaces.entity.IPermissionHolder;
import io.github.wysohn.rapidframework2.core.interfaces.entity.IPluginEntity;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.manager.NamedElement;

import java.util.*;

public class Group implements IPermissionHolder, IPluginEntity, NamedElement {
    private final UUID uuid;

    private final Set<UUID> children = new HashSet<>();
    private final Map<String, Object> metaData = new HashMap<>();

    private UUID ownerUuid;
    private UUID parentUuid;
    private String displayName;
    private String mark;

    public Group() {
        this.uuid = UUID.randomUUID();
    }

    public Group(UUID parentUuid) {
        this.uuid = UUID.randomUUID();
        this.parentUuid = parentUuid;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public Set<UUID> getChildren() {
        return children;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setParentUuid(UUID parentUuid) {
        this.parentUuid = parentUuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    @Override
    public UUID getParentUuid() {
        return parentUuid;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getStringKey() {
        return null;
    }
}
