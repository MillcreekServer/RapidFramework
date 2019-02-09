package io.github.wysohn.rapidframework.pluginbase.manager.region;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.language.DefaultLanguages;
import io.github.wysohn.rapidframework.pluginbase.manager.ManagerElementCaching;
import io.github.wysohn.rapidframework.pluginbase.manager.ManagerElementCaching.CacheDeleteHandle;
import io.github.wysohn.rapidframework.pluginbase.manager.ManagerElementCaching.CacheUpdateHandle;
import io.github.wysohn.rapidframework.pluginbase.manager.ManagerElementCaching.SaveHandle;
import io.github.wysohn.rapidframework.pluginbase.objects.Area;
import io.github.wysohn.rapidframework.pluginbase.objects.ClaimInfo;
import io.github.wysohn.rapidframework.pluginbase.objects.SimpleChunkLocation;
import io.github.wysohn.rapidframework.pluginbase.objects.SimpleLocation;
import io.github.wysohn.rapidframework.utils.locations.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;

import java.util.*;

public abstract class ManagerRegion<PB extends PluginBase, V extends ClaimInfo> extends ManagerElementCaching<PB, Area, V>
        implements Listener {
    private final Map<SimpleChunkLocation, Set<Area>> regionsCache = new HashMap<>();
    private final Set<Class<? extends Event>> registeredEventTypes = new HashSet<>();

    private GeneralEventHandle generalEventHandle = (e, loc, entity) -> {
        return false;
    };

    public ManagerRegion(PB base, int loadPriority) {
        super(base, loadPriority);
    }

    @Override
    protected void onEnable() throws Exception {
        super.onEnable();
    }

    @Override
    protected Area createKeyFromString(String str) {
        return Area.fromString(str);
    }

    @Override
    protected CacheUpdateHandle<Area, V> getUpdateHandle() {
        return updateHandle;
    }

    @Override
    protected CacheDeleteHandle<Area, V> getDeleteHandle() {
        return deleteHandle;
    }

    protected void setGeneralEventHandle(GeneralEventHandle generalEventHandle) {
        this.generalEventHandle = generalEventHandle;
    }

    public Set<Class<? extends Event>> getRegisteredEventTypes() {
        return registeredEventTypes;
    }

    protected <T extends Event> void initEvent(Class<? extends T> event, final EventHandle<T> eventHandle) {
        if(registeredEventTypes.add(event)) {
            Bukkit.getPluginManager().registerEvent(event, this, EventPriority.NORMAL, new EventExecutor() {

                @Override
                public void execute(Listener arg0, Event arg1) throws EventException {
                    if(event != arg1.getClass())
                        return;

                    Location loc = eventHandle.getLocation((T) arg1);
                    if (loc == null)
                        return;

                    Entity cause = eventHandle.getCause((T) arg1);

                    // canceled
                    if (generalEventHandle != null && generalEventHandle.preEvent(arg1, loc, cause)) {
                        return;
                    }

                    SimpleLocation sloc = LocationUtil.convertToSimpleLocation(loc);

                    V claim = ManagerRegion.this.getAreaInfo(sloc);
                    if (claim == null)
                        return;

                    // don't protect if chunk is public
                    if (claim.isPublic())
                        return;

                    if (cause instanceof Player) {
                        Player p = (Player) cause;

                        if (p.isOp())
                            return;

                        UUID uuid = p.getUniqueId();

                        if (uuid.equals(claim.getOwner()))
                            return;

                        if (claim.getTrusts().contains(uuid))
                            return;
                    }

                    // canceled
                    if (generalEventHandle != null) {
                        generalEventHandle.postEvent(arg1);
                    }

                    if(arg1 instanceof Cancellable && ((Cancellable) arg1).isCancelled() && cause instanceof Player) {
                        base.sendMessage((Player)cause, DefaultLanguages.General_NotEnoughPermission);
                    }
                }

            }, base);
        }
    }

    public V getAreaInfo(SimpleLocation sloc) {
        if (sloc == null)
            return null;

        SimpleChunkLocation scloc = new SimpleChunkLocation(sloc);
        synchronized (regionsCache) {
            if (!regionsCache.containsKey(scloc))
                return null;
        }

        synchronized (regionsCache) {
            Set<Area> areas = regionsCache.get(scloc);
            if (areas != null) {
                for (Area area : areas)
                    if (area.isInThisArea(sloc))
                        return this.get(area, false);
            }
        }

        return null;
    }

    public V getAreaInfo(String name) {
        if (name == null)
            return null;

        return this.get(name, false);
    }

    /**
     * Set info of area. If 'info' is null, the data connected with key 'area'
     * will be removed.
     *
     * @param area
     * @param info
     */
    public void setAreaInfo(Area area, V info) {
        // first schedule update task
        this.save(area, info);

        synchronized (regionsCache) {
            // clean up cache
            removeAreaCache(area);

            // don't cache again if deleting info
            if (info != null) {
                // re-cache claim info
                setAreaCache(area);
            }
        }
    }

    public void removeAreaInfo(Area area) {
        // first schedule update task
        this.save(area, null);

        synchronized (regionsCache) {
            // clean up cache
            removeAreaCache(area);
        }
    }

    /**
     *
     * @param before
     * @param after
     * @return false if area info of 'before' doesn't exist, or area info of
     *         'after' already exist; true otherwise.
     */
    public boolean resizeArea(Area before, Area after) {
        if (this.get(after, false) != null)
            return false;

        V info = this.get(before, false);
        if (info == null)
            return false;

        // first schedule update task
        this.save(before, null);
        this.save(after, info, new SaveHandle() {

            @Override
            public void preSave() {
                info.setArea(after);
            }

            @Override
            public void postSave() {
                synchronized (regionsCache) {
                    // clean up cache
                    removeAreaCache(before);

                    // re-cache claim info
                    setAreaCache(after);
                }
            }

        });

        return true;
    }

    /**
     * This method is not thread safe.
     *
     * @param area
     */
    private void setAreaCache(Area area) {
        for (SimpleChunkLocation scloc : Area.getAllChunkLocations(area)) {
            Set<Area> areas = regionsCache.get(scloc);
            if (areas == null) {
                areas = new HashSet<>();
                regionsCache.put(scloc, areas);
            }

            areas.add(area);
        }
    }

    /**
     * This method is not thread safe.
     *
     * @param area
     */
    private void removeAreaCache(Area area) {
        synchronized (regionsCache) {
            for (SimpleChunkLocation scloc : Area.getAllChunkLocations(area)) {
                Set<Area> areas = regionsCache.get(scloc);
                if (areas == null)
                    continue;

                areas.remove(area);
            }
        }
    }

    /**
     * get all the area that is conflicting with given area. This does not
     * include the area itself. It's quite a CPU intensive work; use it wisely
     *
     * @param area
     * @return never be null; can be empty if no conflicts are found
     */
    public Set<Area> getConflictingAreas(Area area) {
        Set<Area> conflicts = new HashSet<>();

        Set<SimpleChunkLocation> sclocs = Area.getAllChunkLocations(area);
        synchronized (regionsCache) {
            for (SimpleChunkLocation scloc : sclocs) {
                Set<Area> areas = this.regionsCache.get(scloc);
                if (areas == null)
                    continue;

                for (Area areaOther : areas) {
                    if (area.equals(areaOther))
                        continue;

                    if (Area.isConflicting(area, areaOther)) {
                        conflicts.add(areaOther);
                    }
                }
            }
        }

        return conflicts;
    }

    private final CacheUpdateHandle<Area, V> updateHandle = new CacheUpdateHandle<Area, V>() {

        @Override
        public V onUpdate(Area key, V original) {
            original.setArea(key);
            setAreaCache(key);
            return null;
        }

    };

    private final CacheDeleteHandle<Area, V> deleteHandle = new CacheDeleteHandle<Area, V>() {

        @Override
        public void onDelete(Area key, V value) {
            removeAreaCache(key);
        }

    };

    /**
     * The gamehandle that is responsible for each Bukkit API events.
     *
     * @author wysohn
     *
     */
    public interface EventHandle<T extends Event> {
        Entity getCause(T e);

        Location getLocation(T e);
    }

    /**
     * This gamehandle can be used to do something before events are passed to each
     * EventHandles. For example, it is tedious to check if a player has bypass
     * permission in every single EventHandles; if you were to use
     * GeneralEventHandle, you can simply check it before the events are passed
     * to the EventHandles.
     *
     * @author wysohn
     *
     */
    protected interface GeneralEventHandle {
        /**
         * This method will be invoked before any events will be hand over to
         * the EventHandles.
         *
         * @param e
         *            event to gamehandle
         * @param cause
         *            the entity caused the event
         * @param loc
         *            location where event occur
         * @return true if event should not be received by all EventHandles;
         *         false otherwise.
         */
        public boolean preEvent(Event e, Location loc, Entity cause);

        /**
         * This method will be invoked after all events are handed over to the
         * EventHandles. Default behavior is canceling event if it's instance of
         * Cancellable
         *
         * @param e
         *            event to gamehandle
         */
        default public void postEvent(Event e) {
            if (e instanceof Cancellable)
                ((Cancellable) e).setCancelled(true);
        }
    }
}
