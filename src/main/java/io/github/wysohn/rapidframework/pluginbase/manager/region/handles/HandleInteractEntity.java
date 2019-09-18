package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class HandleInteractEntity extends DefaultHandle
        implements AbstractManagerRegion.EventHandle<PlayerInteractEntityEvent> {
    public HandleInteractEntity(AbstractManagerRegion rmanager) {
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
