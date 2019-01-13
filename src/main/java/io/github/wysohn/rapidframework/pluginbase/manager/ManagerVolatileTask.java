package io.github.wysohn.rapidframework.pluginbase.manager;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginManager;
import io.github.wysohn.rapidframework.pluginbase.language.DefaultLanguages;
import io.github.wysohn.rapidframework.pluginbase.manager.event.PlayerBlockLocationEvent;
import io.github.wysohn.rapidframework.pluginbase.manager.event.PlayerChunkLocationEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ManagerVolatileTask extends PluginManager<PluginBase> implements Listener {
    private final Map<UUID, Set<BukkitRunnable>> tasks = new ConcurrentHashMap<>();
    private final Map<UUID, Set<BukkitRunnable>> tasksChunk = new ConcurrentHashMap<>();

    public ManagerVolatileTask(PluginBase base, int loadPriority) {
        super(base, loadPriority);
    }

    @Override
    protected void onDisable() throws Exception {
        for(Entry<UUID, Set<BukkitRunnable>> entry : tasks.entrySet()){
            Set<BukkitRunnable> taskSet = entry.getValue();
            for(BukkitRunnable task : taskSet)
                task.cancel();
        }
    }

    @Override
    protected void onEnable() throws Exception {

    }

    @Override
    protected void onReload() throws Exception {

    }

    @EventHandler
    public void onBlockMove(PlayerBlockLocationEvent e){
        if(!tasks.containsKey(e.getPlayer().getUniqueId()))
            return;

        if(cancelAll(tasks, e.getPlayer().getUniqueId())){
            base.sendMessage(e.getPlayer(), DefaultLanguages.VolatileTaskManager_CanceledCauseMoved);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChunkMove(PlayerChunkLocationEvent e){
        if(!tasksChunk.containsKey(e.getPlayer().getUniqueId()))
            return;

        if(cancelAll(tasksChunk, e.getPlayer().getUniqueId())){
            base.sendMessage(e.getPlayer(), DefaultLanguages.VolatileTaskManager_CanceledCauseMoved);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        if(tasks.containsKey(e.getPlayer().getUniqueId()))
            tasks.remove(e.getPlayer().getUniqueId());
        if(tasksChunk.containsKey(e.getPlayer().getUniqueId()))
            tasksChunk.remove(e.getPlayer().getUniqueId());
    }

    public boolean cancelAll(Map<UUID, Set<BukkitRunnable>> tasks, UUID uniqueId) {
        Set<BukkitRunnable> taskSet = tasks.get(uniqueId);
        if(taskSet == null)
            return false;

        for(BukkitRunnable task : taskSet)
            task.cancel();

        tasks.remove(uniqueId);

        return true;
    }

    /**
     * schedule volatile task. Moving one block unit will cancel the whole tasks.
     * @param taskKey this key ensures that no other task can be scheduled with same key until scheduled one is finished.
     * @param player
     * @param run
     * @param delayTicks
     * @return true on success; false if already schedule.
     */
    public boolean scheduleDelayedTask(String taskKey, Player player, Runnable run, long delayTicks){
        Set<BukkitRunnable> taskSet = tasks.get(player.getUniqueId());
        if(taskSet == null){
            taskSet = new HashSet<>();
            tasks.put(player.getUniqueId(), taskSet);
        }

        BukkitRunnable task = new DelayedTask(tasks, taskKey, player, run);
        if(taskSet.contains(task))
            return false;

        long seconds = delayTicks / 20;
        for(long i = 1; i <= seconds; i++){
            BukkitRunnable delayTask = new DelayMessageTask(player, i+"...");
            taskSet.add(delayTask);
            delayTask.runTaskLater(base, (seconds - i) * 20);
        }

        taskSet.add(task);
        task.runTaskLater(base, delayTicks);

        return true;
    }

    /**
     * schedule volatile task. Moving one chunk unit will cancel the whole tasks.
     * @param taskKey this key ensures that no other task can be scheduled with same key until scheduled one is finished.
     * @param player
     * @param run
     * @param delayTicks
     * @return true on success; false if already schedule.
     */
    public boolean scheduleDelayedTaskChunk(String taskKey, Player player, Runnable run, long delayTicks){
        Set<BukkitRunnable> taskSet = tasksChunk.get(player.getUniqueId());
        if(taskSet == null){
            taskSet = new HashSet<>();
            tasksChunk.put(player.getUniqueId(), taskSet);
        }

        BukkitRunnable task = new DelayedTask(tasksChunk, taskKey, player, run);
        if(taskSet.contains(task))
            return false;

        long seconds = delayTicks / 20;
        for(long i = 1; i <= seconds; i++){
            BukkitRunnable delayTask = new DelayMessageTask(player, i+"...");
            taskSet.add(delayTask);
            delayTask.runTaskLater(base, (seconds - i) * 20);
        }

        taskSet.add(task);
        task.runTaskLater(base, delayTicks);

        return true;
    }

    private class DelayMessageTask extends BukkitRunnable {
        private final Player player;
        private final String message;

        public DelayMessageTask(Player player, String message) {
            this.player = player;
            this.message = message;
        }

        @Override
        public void run() {
            player.sendMessage(message);

            Set<BukkitRunnable> taskSet = tasks.get(player.getUniqueId());
            if(taskSet != null){
                taskSet.remove(this);
            }
        }
    }

    private class DelayedTask extends BukkitRunnable {
        private final Map<UUID, Set<BukkitRunnable>> tasks;
        private final String taskKey;

        private final Player player;
        private final Runnable run;

        public DelayedTask(Map<UUID, Set<BukkitRunnable>> tasks, String taskKey, Player player, Runnable run) {
            super();
            this.tasks = tasks;
            this.taskKey = taskKey;
            this.player = player;
            this.run = run;
        }

        @Override
        public void run() {
            run.run();

            cancelAll(tasks, player.getUniqueId());
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((taskKey == null) ? 0 : taskKey.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            DelayedTask other = (DelayedTask) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (taskKey == null) {
                if (other.taskKey != null)
                    return false;
            } else if (!taskKey.equals(other.taskKey))
                return false;
            return true;
        }

        private ManagerVolatileTask getOuterType() {
            return ManagerVolatileTask.this;
        }


    }
}
