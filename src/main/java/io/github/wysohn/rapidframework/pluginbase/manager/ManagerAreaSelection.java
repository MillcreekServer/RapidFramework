package io.github.wysohn.rapidframework.pluginbase.manager;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginManager;
import io.github.wysohn.rapidframework.pluginbase.language.DefaultLanguages;
import io.github.wysohn.rapidframework.pluginbase.objects.Area;
import io.github.wysohn.rapidframework.pluginbase.objects.SimpleLocation;
import io.github.wysohn.rapidframework.utils.BukkitUtil;
import io.github.wysohn.rapidframework.utils.locations.LocationUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class ManagerAreaSelection extends PluginManager<PluginBase> implements Listener {
    protected final Set<UUID> selecting = new HashSet<>();
    protected final Map<UUID, SimpleLocation> leftPosition = new HashMap<>();
    protected final Map<UUID, SimpleLocation> rightPosition = new HashMap<>();

    public ManagerAreaSelection(PluginBase plugin, int priority) {
        super(plugin, priority);
    }

    @Override
    protected void onEnable() throws Exception {

    }

    @Override
    protected void onDisable() throws Exception {

    }

    @Override
    protected void onReload() throws Exception {

    }

    /**
     * get the smallest point between two coordinates. Smallest means that the x, y,
     * and z are all the minimum value between two coordinates.
     *
     * @param left  coordinate 1
     * @param right coordinate 2
     * @return the smallest between two
     */
    protected static SimpleLocation getSmallest(SimpleLocation left, SimpleLocation right) {
        return new SimpleLocation(left.getWorld(), Math.min(left.getX(), right.getX()),
                Math.min(left.getY(), right.getY()), Math.min(left.getZ(), right.getZ()));
    }

    /**
     * get the largest point between two coordinates. Largest means that the x, y,
     * and z are all the maximum value between two coordinates.
     *
     * @param left  coordinate 1
     * @param right coordinate 2
     * @return the largest between two
     */
    protected static SimpleLocation getLargest(SimpleLocation left, SimpleLocation right) {
        return new SimpleLocation(right.getWorld(), Math.max(left.getX(), right.getX()),
                Math.max(left.getY(), right.getY()), Math.max(left.getZ(), right.getZ()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        resetSelections(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!selecting.contains(uuid))
            return;

        e.setCancelled(true);

        if (!BukkitUtil.isLeftHandClick(e))
            return;

        SimpleLocation sloc = LocationUtil.convertToSimpleLocation(e.getClickedBlock().getLocation());

        ClickResult result = null;
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            result = onClick(ClickAction.LEFT_CLICK_BLOCK, uuid, sloc);
        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            result = onClick(ClickAction.RIGHT_CLICK_BLOCK, uuid, sloc);
        }

        if (result != null) {
            switch (result) {
                case DIFFERENTWORLD:
                    base.sendMessage(player, DefaultLanguages.Manager_AreaSelection_DIFFERENTWORLD);
                    break;
                case COMPLETE:
                    SimpleLocation left = leftPosition.get(uuid);
                    SimpleLocation right = rightPosition.get(uuid);

                    SimpleLocation smallest = getSmallest(left, right);
                    SimpleLocation largest = getLargest(left, right);

                    base.lang.addString(smallest.toString());
                    base.lang.addString(largest.toString());
                    base.sendMessage(player, DefaultLanguages.Manager_AreaSelection_COMPLETE);
                    break;
                case LEFTSET:
                    base.sendMessage(player, DefaultLanguages.Manager_AreaSelection_LEFTSET);
                    break;
                case RIGHTSET:
                    base.sendMessage(player, DefaultLanguages.Manager_AreaSelection_RIGHTSET);
                    break;
            }
        }
    }

    /**
     * gets called when player clicks on a block. <b>This should be called manually
     * by the child class upon player interaction event.</b>
     *
     * @param action the {@link ClickAction} associated with this player
     *               interaction.
     * @param uuid   the uuid of player
     * @param sloc   location where interaction occurred
     * @return the result as {@link ClickResult}
     */
    protected ClickResult onClick(ClickAction action, UUID uuid, SimpleLocation sloc) {
        if (action == ClickAction.LEFT_CLICK_BLOCK) {
            leftPosition.put(uuid, sloc);
        } else if (action == ClickAction.RIGHT_CLICK_BLOCK) {
            rightPosition.put(uuid, sloc);
        }

        SimpleLocation left = leftPosition.get(uuid);
        SimpleLocation right = rightPosition.get(uuid);
        if (left != null && right != null) {
            if (!left.getWorld().equals(right.getWorld())) {
                return ClickResult.DIFFERENTWORLD;
            }

            return ClickResult.COMPLETE;
        } else if (left != null) {
            return ClickResult.LEFTSET;
        } else if (right != null) {
            return ClickResult.RIGHTSET;
        } else {
            return null;
        }
    }

    /**
     * @param player
     * @return true if on; false if off
     */
    public boolean toggleSelection(UUID uuid) {
        if (selecting.contains(uuid)) {
            selecting.remove(uuid);
            resetSelections(uuid);
            return false;
        } else {
            selecting.add(uuid);
            return true;
        }
    }

    public void resetSelections(UUID uuid) {
        selecting.remove(uuid);
        leftPosition.remove(uuid);
        rightPosition.remove(uuid);
    }

    /**
     * @param player
     * @return null if invalid selection; Area if done
     */
    public Area getSelection(UUID uuid) {
        SimpleLocation left = leftPosition.get(uuid);
        SimpleLocation right = rightPosition.get(uuid);

        if (left != null && right != null) {
            if (!left.getWorld().equals(right.getWorld())) {
                return null;
            }

            SimpleLocation smallest = getSmallest(left, right);
            SimpleLocation largest = getLargest(left, right);

            return new Area(smallest, largest);
        } else {
            return null;
        }
    }

    public enum ClickAction {
        /**
         * Left clicked on block
         **/
        LEFT_CLICK_BLOCK,
        /**
         * Right clicked on block
         **/
        RIGHT_CLICK_BLOCK;
    }

    public enum ClickResult {
        /**
         * When two selections are in different worlds
         **/
        DIFFERENTWORLD,
        /**
         * Two coordinates are ready
         **/
        COMPLETE,
        /**
         * Only left clicked coordinate is ready
         **/
        LEFTSET,
        /**
         * Only right clicked coordinated is ready
         **/
        RIGHTSET;
    }

}
