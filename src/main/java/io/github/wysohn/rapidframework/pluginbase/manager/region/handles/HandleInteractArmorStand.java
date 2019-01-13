package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion.EventHandle;

public class HandleInteractArmorStand extends DefaultHandle implements ManagerRegion.EventHandle<PlayerArmorStandManipulateEvent> {
    public HandleInteractArmorStand(ManagerRegion rmanager) {
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
