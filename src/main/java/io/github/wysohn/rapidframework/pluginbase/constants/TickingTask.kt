package io.github.wysohn.rapidframework.pluginbase.constants

import io.github.wysohn.rapidframework.pluginbase.PluginBase
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

/**
 * BukkitRunnable that allows some custom actions for each ticks and actions when the ticking ends
 *
 * @param base PluginBase
 * @param ticksAfter ticks delayed before task ends
 * @param onTick task to perform for each tick. The input(Long) indicates how many ticks left until the task finishes.
 * The boolean returned will inform this task to be cancelled right away or not. Returning false will cancel the task; returning
 * true will continue the delayed task.
 * @param runnable task to perform when delay is finished. Can be null if nothing will happen after the delay.
 */
class TickingTask(val base: PluginBase, val ticksAfter: Long, val onTick: (Long)->Boolean,
                  val runnable: ()->Unit? = {})
    : BukkitRunnable(){
    companion object {

    }

    private var currentTick = ticksAfter;
    override fun run() {
        currentTick--;

        if(onTick(currentTick)){
            Bukkit.getScheduler().runTaskLater(base, {
                if(currentTick <= 0){
                    runnable();
                }else{
                    run();
                }
            }, 1L);
        }
    }
}