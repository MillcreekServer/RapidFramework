package io.github.wysohn.rapidframework.pluginbase.manager;

import java.util.UUID;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginLanguage.Language;
import io.github.wysohn.rapidframework.pluginbase.PluginLanguage.PreParseHandle;
import io.github.wysohn.rapidframework.pluginbase.objects.Group;
import io.github.wysohn.rapidframework.utils.Validation;

public abstract class AbstractManagerGroup<PB extends PluginBase, V extends Group>
		extends ManagerElementCaching<PB, UUID, V> {

	public AbstractManagerGroup(PB base, int loadPriority) {
		super(base, loadPriority);
	}
	
	@Override
	protected UUID createKeyFromString(String str) {
		return UUID.fromString(str);
	}
	
	protected abstract V createNewGroup(UUID ownerUuid);

	/**
	 * Attempt to create a new faction with given name and owner
	 * set to the given UUID
	 * @param name name of the new faction. Shouldn't be null.
	 * @param ownerUuid the owner's uuid. Shouldn't be null
	 * @param onFinish task to perform before saving it
	 * @return true if created; false if already a group exist with the name.
	 */
	public boolean create(String name, UUID ownerUuid, GroupHandle<V> onFinish) {
		Validation.validate(name);
		Validation.validate(ownerUuid);
		
		V group = this.get(name);
		if(group != null)
			return false;
		
		group = createNewGroup(ownerUuid);
		group.setDisplayName(name);
		group.getChildren().add(ownerUuid);
		
		if(onFinish.accept(group)) {
			this.save(group.getUuid(), group);
		}

		return true;
	}
	
	/**
	 * Disband the group with given name.
	 * @param name the name of faction
	 * @param onFinish task to perform before deleting the group
	 * @return true if disbanded; false if not exist
	 */
	public boolean disband(String name, GroupHandle<V> onFinish) {
		V group = this.get(name);
		return disband(group, onFinish);
	}
	
	/**
	 * Disband the group with given name.
	 * @param uuid the uuid of faction
	 * @param onFinish task to perform before deleting the group
	 * @return true if disbanded; false if not exist
	 */
	public boolean disband(UUID uuid, GroupHandle<V> onFinish) {
		V group = this.get(uuid);
		return disband(group, onFinish);
	}
	
	private boolean disband(V group, GroupHandle<V> onFinish) {
		if(group == null)
			return false;
		
		if(onFinish.accept(group)) {
			this.save(group.getUuid(), null);
		}
		
		return true;
	}
	
	/**
	 * Handle which will be called before the group is saved.
	 * @author wysohn
	 *
	 * @param <V>
	 */
	@FunctionalInterface
	public interface GroupHandle<V extends Group>{
		/**
		 * Called before any final changes to the group happens.
		 * @param group the group that is about to get saved
		 * @return true to proceed; false to interrupt and cancel
		 */
		boolean accept(V group);
	}
}
