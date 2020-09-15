package io.github.wysohn.rapidframework3.bukkit.plugin.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework3.bukkit.data.BukkitWrapper;
import io.github.wysohn.rapidframework3.core.chat.AbstractChatManager;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;
import io.github.wysohn.rapidframework3.core.inject.factory.IStorageFactory;
import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.interfaces.chat.IPlaceholderSupport;
import io.github.wysohn.rapidframework3.utils.JarUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;
import java.util.stream.Collectors;

@Singleton
public class ManagerChat extends AbstractChatManager implements Listener {
    @Inject
    public ManagerChat(PluginMain main,
                       @PluginDirectory File pluginDir,
                       IStorageFactory storageFactory,
                       IPlaceholderSupport placeholderSupport) {
        super(main, pluginDir, storageFactory, placeholderSupport);
    }

    @Override
    public void preload() throws Exception {
        JarUtil.copyFromJar(ManagerChat.class,
                CHAT_YML,
                main().getPluginDirectory(),
                JarUtil.CopyOption.COPY_IF_NOT_EXIST);

        if (!main().conf().get("chat.enable").isPresent()) {
            main().conf().put("chat.enable", false);
        }
    }

    //Msg[x1,x2,x3,...] Msg2[x1,x2,x3,...]
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!main().conf().get("chat.enable")
                .map(Boolean.class::cast)
                .orElse(false))
            return;

        try {
            onChat(BukkitWrapper.player(event.getPlayer()),
                    event.getRecipients().stream()
                            .map(BukkitWrapper::sender)
                            .collect(Collectors.toList()),
                    event.getMessage());

            event.setCancelled(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
