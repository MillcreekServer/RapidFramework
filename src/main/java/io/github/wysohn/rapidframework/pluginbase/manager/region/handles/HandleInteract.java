package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion.EventHandle;

public class HandleInteract extends DefaultHandle implements AbstractManagerRegion.EventHandle<PlayerInteractEvent> {
    public HandleInteract(AbstractManagerRegion rmanager) {
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
