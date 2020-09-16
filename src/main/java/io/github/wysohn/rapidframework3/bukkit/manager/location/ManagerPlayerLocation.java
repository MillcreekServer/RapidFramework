/*******************************************************************************
 *     Copyright (C) 2017 wysohn
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package io.github.wysohn.rapidframework3.bukkit.manager.location;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework3.bukkit.events.PlayerBlockLocationEvent;
import io.github.wysohn.rapidframework3.bukkit.events.PlayerChunkLocationEvent;
import io.github.wysohn.rapidframework3.core.main.Manager;
import io.github.wysohn.rapidframework3.data.SimpleChunkLocation;
import io.github.wysohn.rapidframework3.data.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ManagerPlayerLocation extends Manager {
    private static PlayerTracker TRACKER;

    @Inject
    public ManagerPlayerLocation() {
    }

    public static SimpleLocation getCurrentBlockLocation(UUID uuid) {
        return TRACKER.getCurrentBlockLocation(uuid);
    }

    @Override
    public void enable() throws Exception {
        if (TRACKER == null) {
            TRACKER = new PlayerTracker();
        }
    }

    @Override
    public void load() throws Exception {

    }

    @Override
    public void disable() throws Exception {

    }

    private static final class PlayerTracker implements Listener {
        private transient Map<UUID, SimpleLocation> locations = new ConcurrentHashMap<>();

        /**
         * get location of player
         *
         * @param uuid uuid of player
         * @return the location. If the player just logged in, it might be null.
         */
        public SimpleLocation getCurrentBlockLocation(UUID uuid) {
            return locations.get(uuid);
        }

        /**
         * set current location of the player
         *
         * @param uuid the player's uuid
         * @param sloc the location where player is at
         */
        protected void setCurrentBlockLocation(UUID uuid, SimpleLocation sloc) {
            locations.put(uuid, sloc);
        }

        /**
         * remove the current location of the player.
         *
         * @param uuid the player's uuid
         */
        protected void removeCurrentBlockLocation(UUID uuid) {
            locations.remove(uuid);
        }

        private SimpleLocation convertToSimpleLocation(Location loc) {
            return new SimpleLocation(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
        }

        private Location convertToBukkitLocation(SimpleLocation from) {
            return Optional.ofNullable(Bukkit.getWorld(from.getWorld()))
                    .map(world -> new Location(world, from.getX(), from.getY(), from.getZ()))
                    .orElseThrow(() -> new RuntimeException("Invalid SimpleLocation"));
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onJoin(PlayerJoinEvent e) {
            Player player = e.getPlayer();
            Location loc = player.getLocation();
            SimpleLocation sloc = convertToSimpleLocation(loc);
            setCurrentBlockLocation(player.getUniqueId(), sloc);
        }

        @EventHandler
        public void onSpawn(PlayerRespawnEvent e) {
            Player player = e.getPlayer();
            Location loc = player.getLocation();
            SimpleLocation sloc = convertToSimpleLocation(loc);
            setCurrentBlockLocation(player.getUniqueId(), sloc);
        }

        @EventHandler
        public void onTeleport(PlayerChangedWorldEvent e) {
            Player player = e.getPlayer();
            Location loc = player.getLocation();
            SimpleLocation sloc = convertToSimpleLocation(loc);
            setCurrentBlockLocation(player.getUniqueId(), sloc);
        }

        @EventHandler(priority = EventPriority.HIGH)
        public void onQuit(PlayerQuitEvent e) {
            Player player = e.getPlayer();
            removeCurrentBlockLocation(player.getUniqueId());
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onMove(PlayerMoveEvent e) {
            if (e.getTo() == e.getFrom())
                return;

            Player player = e.getPlayer();

            SimpleLocation from = getCurrentBlockLocation(player.getUniqueId());
            SimpleLocation to = convertToSimpleLocation(e.getTo());

            if (from.equals(to))
                return;

            SimpleChunkLocation fromChunk = new SimpleChunkLocation(from);
            SimpleChunkLocation toChunk = new SimpleChunkLocation(to);

            boolean cancelled = false;
            if (!fromChunk.equals(toChunk)) {
                PlayerChunkLocationEvent pcle = new PlayerChunkLocationEvent(player, fromChunk, toChunk);
                Bukkit.getPluginManager().callEvent(pcle);
                if (pcle.isCancelled())
                    cancelled = true;
            }

            PlayerBlockLocationEvent pble = new PlayerBlockLocationEvent(player, from, to);
            Bukkit.getPluginManager().callEvent(pble);
            if (pble.isCancelled())
                cancelled = true;

            if (cancelled) {
                e.setCancelled(true);

                Location loc = convertToBukkitLocation(from);
                loc.setPitch(e.getPlayer().getLocation().getPitch());
                loc.setYaw(e.getPlayer().getLocation().getPitch());
                e.setFrom(loc);
                e.setTo(loc);
            } else {
                setCurrentBlockLocation(player.getUniqueId(), to);
            }
        }
    }
}
