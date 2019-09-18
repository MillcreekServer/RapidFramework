package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEvent;

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
        if (e.getClickedBlock() == null)
            return null;

        return e.getClickedBlock().getLocation();
    }
}
