package io.github.wysohn.rapidframework.pluginbase.manager;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginManager;
import io.github.wysohn.rapidframework.pluginbase.language.DefaultLanguages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ManagerTargetBlock extends PluginManager<PluginBase> implements Listener {
    private final Map<UUID, TargetCallback> callbackMap = new HashMap<>();

    public ManagerTargetBlock(PluginBase base, int loadPriority) {
        super(base, loadPriority);
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
     * Request a block which player will click on. The callback will be called with the specific interaction event
     *
     * @param player
     * @param callback
     */
    public void requestTargetBlock(Player player, TargetCallback callback) {
        callbackMap.put(player.getUniqueId(), callback);
        base.sendMessage(player, DefaultLanguages.TargetBlockManager_ReadyToClick);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        UUID uuid = e.getPlayer().getUniqueId();
        if(!callbackMap.containsKey(uuid))
            return;

        e.setCancelled(true);

        if(e.getPlayer().isSneaking()) {
            callbackMap.remove(uuid);
            base.sendMessage(e.getPlayer(), DefaultLanguages.TargetBlockManager_Canceled);
        }else {
            callbackMap.remove(uuid).onClick(e);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        callbackMap.remove(e.getPlayer().getUniqueId());
    }

    public interface TargetCallback{
        /**
         * This blocks the server thread.
         * @param e the direct reference where the Listener interface received the event
         */
        void onClick(PlayerInteractEvent e);
    }
}
