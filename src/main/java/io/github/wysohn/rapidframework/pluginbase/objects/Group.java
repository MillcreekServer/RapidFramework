package io.github.wysohn.rapidframework.pluginbase.objects;

import io.github.wysohn.rapidframework.pluginbase.manager.ManagerElementCaching;
import io.github.wysohn.rapidframework.pluginbase.objects.permissions.PermissionHolder;

import java.util.*;

public class Group extends PermissionHolder implements ManagerElementCaching.NamedElement {
    private final UUID uuid = UUID.randomUUID();
    private final Set<UUID> children = new HashSet<>();
    private final Map<String, Object> metaData = new LinkedHashMap<>();
    private UUID parentUuid;
    private String displayName;
    private UUID leader;

    public Group(UUID leader) {
        this.leader = leader;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public UUID getParentUuid() {
        return parentUuid;
    }

    @Override
    protected void setParentUuid(UUID parentUuid) {
        this.parentUuid = parentUuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public Set<UUID> getChildren() {
        return children;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }
}
