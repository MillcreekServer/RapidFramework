package io.github.wysohn.rapidframework3.bukkit.plugin.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework3.bukkit.data.BukkitWrapper;
import io.github.wysohn.rapidframework3.core.chat.AbstractChatManager;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginLogger;
import io.github.wysohn.rapidframework3.core.inject.factory.IStorageFactory;
import io.github.wysohn.rapidframework3.core.language.ManagerLanguage;
import io.github.wysohn.rapidframework3.core.main.ManagerConfig;
import io.github.wysohn.rapidframework3.interfaces.chat.IPlaceholderSupport;
import io.github.wysohn.rapidframework3.utils.JarUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
public class ManagerChat extends AbstractChatManager implements Listener {
    private final File pluginDir;
    private final ManagerConfig config;

    @Inject
    public ManagerChat(ManagerLanguage lang,
                       @PluginDirectory File pluginDir,
                       @PluginLogger Logger logger,
                       IStorageFactory storageFactory,
                       IPlaceholderSupport placeholderSupport,
                       ManagerConfig config) {
        super(lang, pluginDir, logger, storageFactory, placeholderSupport);
        this.pluginDir = pluginDir;
        this.config = config;
    }

    @Override
    public void preload() throws Exception {
        JarUtil.copyFromJar(ManagerChat.class,
                CHAT_YML,
                pluginDir,
                JarUtil.CopyOption.COPY_IF_NOT_EXIST);

        if (!config.get("chat.enable").isPresent()) {
            config.put("chat.enable", false);
        }
    }

    //Msg[x1,x2,x3,...] Msg2[x1,x2,x3,...]
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!config.get("chat.enable")
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
