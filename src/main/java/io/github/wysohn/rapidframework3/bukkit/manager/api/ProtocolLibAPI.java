package io.github.wysohn.rapidframework3.bukkit.manager.api;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import io.github.wysohn.rapidframework3.bukkit.data.BukkitPlayer;
import io.github.wysohn.rapidframework3.core.api.ExternalAPI;
import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.core.message.Message;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ProtocolLibAPI extends ExternalAPI {
    public ProtocolLibAPI(PluginMain main, String pluginName) {
        super(main, pluginName);
    }

    @Override
    public void enable() throws Exception {

    }

    @Override
    public void load() throws Exception {

    }

    @Override
    public void disable() throws Exception {

    }

    public void send(BukkitPlayer player, Message[] messages) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CHAT);

        colorize(messages);

        TextComponent jsonMessages = toJsonMessage(messages);
        packet.getChatComponents().write(0,
                WrappedChatComponent.fromJson(ComponentSerializer.toString(jsonMessages)));

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player.getSender(), packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void colorize(Message[] messages) {
        Arrays.stream(messages).forEach(message ->
                message.colorize(str -> ChatColor.translateAlternateColorCodes('&', str)));
    }

    private TextComponent toJsonMessage(Message[] messages) {
        TextComponent head = new TextComponent();
        for (int i = 0; i < messages.length; i++) {
            TextComponent jsonMessage = new TextComponent();
            Message message = messages[i];

            jsonMessage.setText(message.getString());
            if (message.getClick_OpenFile() != null) {
                jsonMessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, message.getClick_OpenFile()));
            }
            if (message.getClick_OpenUrl() != null) {
                jsonMessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, message.getClick_OpenUrl()));
            }
            if (message.getClick_RunCommand() != null) {
                jsonMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, message.getClick_RunCommand()));
            }
            if (message.getClick_SuggestCommand() != null) {
                jsonMessage.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, message.getClick_SuggestCommand()));
            }
            if (message.getHover_ShowText() != null) {
                jsonMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, toComponent(message.getHover_ShowText())));
            }
            if (message.getHover_ShowAchievement() != null) {
                jsonMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT, toComponent(message.getHover_ShowAchievement())));
            }
            if (message.getHover_ShowItem() != null) {
                jsonMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, toComponent(message.getHover_ShowItem())));
            }

            head.addExtra(jsonMessage);
        }

        return head;
    }

    private BaseComponent[] toComponent(String string) {
        return new BaseComponent[]{new TextComponent(string)};
    }
}
