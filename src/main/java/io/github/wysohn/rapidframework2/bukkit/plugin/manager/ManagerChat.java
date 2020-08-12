package io.github.wysohn.rapidframework2.bukkit.plugin.manager;

import io.github.wysohn.rapidframework2.bukkit.main.config.ConfigFileSession;
import io.github.wysohn.rapidframework2.bukkit.main.objects.BukkitWrapper;
import io.github.wysohn.rapidframework2.core.manager.chat.AbstractChatManager;
import io.github.wysohn.rapidframework2.core.manager.chat.IPlaceholderSupport;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import util.JarUtil;

import java.io.File;
import java.util.stream.Collectors;

public class ManagerChat extends AbstractChatManager implements Listener {
    public static final String CHAT_YML = "chat.yml";

    public ManagerChat(int loadPriority, File pluginFolder, IPlaceholderSupport placeholderSupport) {
        super(loadPriority,
                new ConfigFileSession(new File(pluginFolder, CHAT_YML)),
                placeholderSupport);
    }

    @Override
    public void preload() throws Exception {
        JarUtil.copyFromJar(ManagerChat.class,
                CHAT_YML,
                main().getPluginDirectory(),
                JarUtil.CopyOption.COPY_IF_NOT_EXIST);

        if (!main().conf().get("chat.enable").isPresent()) {
            main().conf().put("chat.enable", false);
            main().conf().save();
        }
    }

    //Msg[x1,x2,x3,...] Msg2[x1,x2,x3,...]
    @EventHandler(priority = EventPriority.LOW)
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
