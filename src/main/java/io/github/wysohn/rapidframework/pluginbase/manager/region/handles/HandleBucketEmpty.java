package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion.EventHandle;

public class HandleBucketEmpty extends DefaultHandle implements ManagerRegion.EventHandle<PlayerBucketEmptyEvent> {
    public HandleBucketEmpty(ManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(PlayerBucketEmptyEvent e) {
        return e.getPlayer();
    }

    @Override
    public Location getLocation(PlayerBucketEmptyEvent e) {
        return e.getBlockClicked().getLocation();
    }
}
