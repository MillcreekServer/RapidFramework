package io.github.wysohn.rapidframework.pluginbase.objects;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import io.github.wysohn.rapidframework.pluginbase.manager.ManagerElementCaching.NamedElement;
import io.github.wysohn.rapidframework.pluginbase.objects.permissions.PermissionHolder;

public class Group implements NamedElement, PermissionHolder{
	private final UUID uuid = UUID.randomUUID();
    private final Set<UUID> children = new HashSet<>();
    private final Set<UUID> childGroups = new HashSet<>();
	
    private UUID parentUuid;
    
	private String displayName;
	private UUID leader;
	
	private UUID defaultGroup;
	 
	public Group(UUID leader) {
		this.leader = leader;
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public Set<UUID> getChildren() {
		return children;
	}

	public Set<UUID> getChildGroups() {
		return childGroups;
	}

	public UUID getParentUuid() {
		return parentUuid;
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

	public UUID getLeader() {
		return leader;
	}

	public void setLeader(UUID leader) {
		this.leader = leader;
	}

	public UUID getDefaultGroup() {
		return defaultGroup;
	}

	public void setDefaultGroup(UUID defaultGroup) {
		this.defaultGroup = defaultGroup;
	}

	@Override
	public String getName() {
		return displayName;
	}

	@Override
	public String toString() {
		return "Group [uuid=" + uuid + ", children=" + children + ", childGroups=" + childGroups + ", displayName="
				+ displayName + ", leader=" + leader + ", defaultGroup=" + defaultGroup + "]";
	}
}
