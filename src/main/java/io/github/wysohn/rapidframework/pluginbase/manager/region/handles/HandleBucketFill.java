package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerBucketFillEvent;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion.EventHandle;

public class HandleBucketFill extends DefaultHandle implements AbstractManagerRegion.EventHandle<PlayerBucketFillEvent> {
    public HandleBucketFill(AbstractManagerRegion rmanager) {
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
