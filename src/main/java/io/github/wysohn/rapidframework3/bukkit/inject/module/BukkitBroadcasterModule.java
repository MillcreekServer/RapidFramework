package io.github.wysohn.rapidframework3.bukkit.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.bukkit.data.BukkitWrapper;
import io.github.wysohn.rapidframework3.interfaces.message.IBroadcaster;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class BukkitBroadcasterModule extends AbstractModule {
    @Provides
    IBroadcaster broadcaster() {
        return fn -> Bukkit.getOnlinePlayers().stream()
                .filter(OfflinePlayer::isOnline)
                .map(BukkitWrapper::player)
                .forEach(fn);
    }
}
