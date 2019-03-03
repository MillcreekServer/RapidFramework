package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion.EventHandle;

public class HandleBucketEmpty extends DefaultHandle implements AbstractManagerRegion.EventHandle<PlayerBucketEmptyEvent> {
    public HandleBucketEmpty(AbstractManagerRegion rmanager) {
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
