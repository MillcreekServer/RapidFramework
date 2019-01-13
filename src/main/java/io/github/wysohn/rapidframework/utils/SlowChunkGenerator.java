package io.github.wysohn.rapidframework.utils;

import io.github.wysohn.rapidframework.main.FakePlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

public class SlowChunkGenerator {
    public static void loadChunkSlowly(final World world, final int i, final int j) {
        final int range = Bukkit.getServer().getViewDistance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int x = -range; x <= range; x++) {
                    for (int z = -range; z <= range; z++) {
                        final int currX = i + x;
                        final int currZ = j + z;

                        BukkitTask task = Bukkit.getScheduler().runTask(FakePlugin.instance, new Runnable() {
                            @Override
                            public void run() {
                                world.loadChunk(currX, currZ, true);
                            }
                        });

                        while (Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId())) {
                            try {
                                Thread.sleep(100L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }) {
            {
                setPriority(Thread.MIN_PRIORITY);
            }
        }.start();
    }
}
