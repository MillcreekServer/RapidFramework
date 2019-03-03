package io.github.wysohn.rapidframework.pluginbase.manager;

import java.util.UUID;

import io.github.wysohn.rapidframework.database.Database.DatabaseFactory;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.objects.Group;
import io.github.wysohn.rapidframework.pluginbase.objects.permissions.PermissionHolder;
import io.github.wysohn.rapidframework.pluginbase.objects.permissions.PermissionHolderProvider;

public class ManagerGroup extends AbstractManagerGroup<PluginBase, Group> implements PermissionHolderProvider {

	public ManagerGroup(PluginBase base, int loadPriority) {
		super(base, loadPriority, createDatabaseFactory(base, "Groups", Group.class));
	}

	@Override
	protected void onEnable() throws Exception {
		super.onEnable();
		
		ManagerCustomPermission mcp = base.getManager(ManagerCustomPermission.class);
		mcp.registerProvider(this);
	}

	@Override
	protected Group createNewGroup(UUID ownerUuid) {
		return new Group(ownerUuid);
	}

	@Override
	protected CacheUpdateHandle<UUID, Group> getUpdateHandle() {
		return null;
	}

	@Override
	protected CacheDeleteHandle<UUID, Group> getDeleteHandle() {
		return null;
	}

	@Override
	public PermissionHolder getPermissionHolder(UUID uuid) {
		return this.get(uuid);
	}

}
