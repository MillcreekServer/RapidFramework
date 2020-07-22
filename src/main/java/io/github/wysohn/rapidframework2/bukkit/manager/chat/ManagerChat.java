package io.github.wysohn.rapidframework2.bukkit.manager.chat;

import io.github.wysohn.rapidframework2.bukkit.main.objects.BukkitWrapper;
import io.github.wysohn.rapidframework2.core.manager.chat.AbstractChatManager;
import io.github.wysohn.rapidframework2.core.manager.chat.IPlaceholderSupport;
import io.github.wysohn.rapidframework2.core.manager.common.AbstractFileSession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.stream.Collectors;

public class ManagerChat extends AbstractChatManager implements Listener {

    private ManagerChat(int loadPriority, AbstractFileSession fileSession, IPlaceholderSupport placeholderSupport) {
        super(loadPriority, fileSession, placeholderSupport);
    }

    //Msg[x1,x2,x3,...] Msg2[x1,x2,x3,...]
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
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
