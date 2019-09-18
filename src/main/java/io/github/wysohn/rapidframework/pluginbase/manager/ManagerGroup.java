package io.github.wysohn.rapidframework.pluginbase.manager;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.objects.Group;
import io.github.wysohn.rapidframework.pluginbase.objects.permissions.PermissionHolder;
import io.github.wysohn.rapidframework.pluginbase.objects.permissions.PermissionHolderProvider;

import java.util.UUID;

public class ManagerGroup<PB extends PluginBase> extends AbstractManagerGroup<PB, Group> implements PermissionHolderProvider {

    public ManagerGroup(PB base, int loadPriority) {
        super(base, loadPriority, createDatabaseFactory(base, "Groups", Group.class));
    }

    @Override
    protected void onEnable() throws Exception {
        super.onEnable();

        ManagerCustomPermission mcp = base.getManager(ManagerCustomPermission.class);
        mcp.registerProvider(this);
    }

    @Override
    protected Group instantiateGroup(UUID ownerUuid) {
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
