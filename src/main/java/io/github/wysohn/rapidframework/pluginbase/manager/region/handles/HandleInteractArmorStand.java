package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

public class HandleInteractArmorStand extends DefaultHandle
        implements AbstractManagerRegion.EventHandle<PlayerArmorStandManipulateEvent> {
    public HandleInteractArmorStand(AbstractManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(PlayerArmorStandManipulateEvent e) {
        return e.getPlayer();
    }

    @Override
    public Location getLocation(PlayerArmorStandManipulateEvent e) {
        return e.getRightClicked().getLocation();
    }
}
