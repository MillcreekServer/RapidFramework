package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion.EventHandle;

public class HandleInteractEntity extends DefaultHandle implements ManagerRegion.EventHandle<PlayerInteractEntityEvent> {
    public HandleInteractEntity(ManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(PlayerInteractEntityEvent e) {
        return e.getPlayer();
    }

    @Override
    public Location getLocation(PlayerInteractEntityEvent e) {
        return e.getRightClicked().getLocation();
    }
}
