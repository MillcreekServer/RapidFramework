package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerBucketFillEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.ManagerRegion.EventHandle;

public class HandleBucketFill extends DefaultHandle implements ManagerRegion.EventHandle<PlayerBucketFillEvent> {
    public HandleBucketFill(ManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(PlayerBucketFillEvent e) {
        return e.getPlayer();
    }

    @Override
    public Location getLocation(PlayerBucketFillEvent e) {
        return e.getBlockClicked().getLocation();
    }
}
