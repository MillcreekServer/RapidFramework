package io.github.wysohn.rapidframework2.core.manager.group;

import io.github.wysohn.rapidframework2.core.interfaces.entity.IPermissionHolder;
import io.github.wysohn.rapidframework2.core.interfaces.entity.IPluginEntity;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.manager.NamedElement;

import java.util.*;

public class Group implements IPermissionHolder, IPluginEntity, NamedElement {
    private final UUID uuid;
    private final UUID ownerUuid;

    private final Set<UUID> children = new HashSet<>();
    private final Map<String, Object> metaData = new HashMap<>();

    private UUID parentUuid;
    private String displayName;

    public Group(UUID ownerUuid) {
        this.uuid = UUID.randomUUID();
        this.ownerUuid = ownerUuid;
    }

    public Group(UUID ownerUuid, UUID parentUuid) {
        this.uuid = UUID.randomUUID();
        this.ownerUuid = ownerUuid;
        this.parentUuid = parentUuid;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
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

    @Override
    public UUID getParentUuid() {
        return parentUuid;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getName() {
        return displayName;
    }
}
