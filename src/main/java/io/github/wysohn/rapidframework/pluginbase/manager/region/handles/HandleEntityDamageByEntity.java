package io.github.wysohn.rapidframework.pluginbase.manager.region.handles;

import io.github.wysohn.rapidframework.pluginbase.manager.region.AbstractManagerRegion;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class HandleEntityDamageByEntity extends DefaultHandle
        implements AbstractManagerRegion.EventHandle<EntityDamageByEntityEvent> {
    public HandleEntityDamageByEntity(AbstractManagerRegion rmanager) {
        super(rmanager);
    }

    @Override
    public Entity getCause(EntityDamageByEntityEvent e) {
        return e.getDamager();
    }

    @Override
    public Location getLocation(EntityDamageByEntityEvent e) {
        Entity attacked = e.getEntity();
        Entity attacker = e.getDamager();

        if (attacked instanceof Monster)
            return null;

        if (attacker instanceof Arrow)
            attacker = (Entity) ((Arrow) attacker).getShooter();

        if (!(attacker instanceof Player))
            return null;

        return attacked.getLocation();
    }
}
