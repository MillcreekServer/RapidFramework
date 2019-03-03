package io.github.wysohn.rapidframework.pluginbase.manager;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.objects.permissions.PermissionHolder;
import io.github.wysohn.rapidframework.pluginbase.objects.permissions.PermissionHolderProvider;
import io.github.wysohn.rapidframework.pluginbase.objects.permissions.Permissions;
import io.github.wysohn.rapidframework.utils.Validation;

/**
 * <p>
 * Simple permission system that will be internally used. Permissions here are
 * not related to the Bukkit API at all, so permissions used here are not
 * reflected from/to Bukkit API's permission system.
 * </p>
 * 
 * <p>
 * Also, "simple" means that the permission string doesn't have such thing like
 * parent and children (like permission.child.child in Bukkit API) but have a
 * plain string that either the permission holder has the permission string or
 * not. Don't expect too much from this manager.
 * </p>
 * 
 * <p>
 * Though, it can detect if the parent group of the permission holder has such
 * permission without the caller explicitly checking on every parents. For
 * example, if player1 is under group1, group1 has permission xyz, and you check
 * if the player1 has permission xyz, it will search through the group hierarchy
 * to see if this player has the required permission. The lower level permission
 * has higher priority when there are duplicating permissions in the hierarchy.
 * </p>
 * 
 * @author wysohn
 *
 */
public class ManagerCustomPermission extends ManagerElementCaching<PluginBase, UUID, Permissions> {
    private final Collection<PermissionHolderProvider> providers = new LinkedList<>();

    public ManagerCustomPermission(PluginBase base, int loadPriority) {
	super(base, loadPriority, createDatabaseFactory(base, "CustomPermissions", Permissions.class));
    }

    @Override
    protected UUID createKeyFromString(String str) {
	return UUID.fromString(str);
    }

    @Override
    protected CacheUpdateHandle<UUID, Permissions> getUpdateHandle() {
	return null;
    }

    @Override
    protected CacheDeleteHandle<UUID, Permissions> getDeleteHandle() {
	return null;
    }

    /**
     * Register PermissionHolderProvider.
     * 
     * @param provider
     * @return true if registered; false if already registered.
     */
    public boolean registerProvider(PermissionHolderProvider provider) {
	if (providers.contains(provider)) {
	    base.getLogger().fine(provider.getClass().getSimpleName() + " duplicate another PermissionHolderProfider.");
	    return false;
	}

	providers.add(provider);
	base.getLogger().fine(provider.getClass().getSimpleName() + " is registered as PermissionHolderProfider.");
	return true;
    }

    private Optional<PermissionHolder> getHolderFromProviders(UUID uuid) {
	for (PermissionHolderProvider provider : providers) {
	    PermissionHolder holder = provider.getPermissionHolder(uuid);
	    if (holder != null)
		return Optional.of(holder);
	}

	return Optional.empty();
    }

    /**
     * Check if the target has at least one of the permission in given permissions.
     * If parent and current target has conflicting permission settings, the current
     * one will be used. (e.g. if parent group of player1 has no permission but
     * player1 has permission X, then player1 is considered to have the permission
     * X)
     * 
     * <p>
     * To use the hierarchical permission system, PermissionHolderProvider must be
     * registered with {@link #registerProvider(PermissionHolderProvider)} if you
     * have custom child class of PermissionHolder
     * </p>
     * 
     * @param target   the target to test permission
     * @param permStrs permissions to test
     * @return true if has at least one of the permission; false otherwise
     */
    public boolean hasPermissionAtLesat(PermissionHolder target, String... permStrs) {
	Validation.validate(target);

	int count = 0;
	while (count++ < 100 && target != null) {
	    Permissions perm = this.get(target.getUuid());
	    if (perm != null) {
		for (String permStr : permStrs) {
		    if (perm.getPermissions().contains(permStr))
			return true;
		}
	    }

	    if (target.getParentUuid() != null) {
		target = getHolderFromProviders(target.getParentUuid()).orElse(null);
	    } else {
		target = null;
	    }
	}

	return false;
    }

    /**
     * Add all permStrs to the target. Duplicates will be ignored
     * 
     * @param target
     * @param permStrs
     */
    public void addPermission(UUID target, String... permStrs) {
	Permissions perm = this.get(target);
	if (perm == null) {
	    perm = new Permissions(target);
	}

	Permissions permCopy = perm;
	Stream.of(permStrs).forEach(p -> permCopy.getPermissions().add(p));

	this.save(target, perm);
    }

    /**
     * Remove the permStr from the target. Nothing happens if permission doesn't
     * exist
     * 
     * @param target
     * @param permStrs
     */
    public void removePermission(UUID target, String permStr) {
	Permissions perm = this.get(target);
	if (perm == null) {
	    return;
	}

	Stream.of(permStr).forEach(p -> perm.getPermissions().remove(p));

	this.save(target, perm);
    }

    /**
     * Remove all permissions
     * 
     * @param target
     */
    public void removePermissions(PermissionHolder target) {
	Permissions perm = this.get(target.getUuid());
	if (perm == null) {
	    return;
	}

	this.save(target.getUuid(), null);
    }
}
