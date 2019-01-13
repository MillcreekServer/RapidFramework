package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion.EventHandle;

public class HandleInteract extends DefaultHandle implements ManagerRegion.EventHandle<PlayerInteractEvent> {
    public HandleInteract(ManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(PlayerInteractEvent e) {
        return e.getPlayer();
    }

    @Override
    public Location getLocation(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null)
            return null;

        return e.getClickedBlock().getLocation();
    }
}
