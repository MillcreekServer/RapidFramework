package io.github.wysohn.rapidframework.pluginbase.api;

import io.github.wysohn.rapidframework.pluginbase.PluginAPISupport.APISupport;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonApiAPI extends APISupport {

    public JsonApiAPI(PluginBase base) {
        super(base);
    }

    @Override
    public boolean init() throws Exception {
        return true;
    }

    public void send(Player player, JsonApiAPI.Message[] messages) {
        TextComponent jsonMessages = toJsonMessage(messages);
        player.spigot().sendMessage(jsonMessages);
    }

    public void send(Player[] player, JsonApiAPI.Message[] messages) {
        Arrays.stream(player).forEach(p -> send(p, messages));
    }

    private TextComponent toJsonMessage(JsonApiAPI.Message[] messages) {
        TextComponent head = new TextComponent();
        for (int i = 0; i < messages.length; i++) {
            TextComponent jsonMessage = new TextComponent();
            Message message = messages[i];

            jsonMessage.setText(message.string);
            if (message.click_OpenFile != null) {
                jsonMessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, message.click_OpenFile));
            }
            if (message.click_OpenUrl != null) {
                jsonMessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, message.click_OpenUrl));
            }
            if (message.click_RunCommand != null) {
                jsonMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, message.click_RunCommand));
            }
            if (message.click_SuggestCommand != null) {
                jsonMessage.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, message.click_SuggestCommand));
            }
            if (message.hover_ShowText != null) {
                jsonMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new BaseComponent[]{new TextComponent(message.hover_ShowText)}));
            }
            if (message.hover_ShowAchievement != null) {
//                jsonMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT,
//                        new BaseComponent[]{new ComponentBuilder()..create()}));
            }
            if (message.hover_ShowItem != null) {
//                jsonMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
//                        new BaseComponent[]{new TextComponent(message.hover_ShowText)}));
            }

            head.addExtra(jsonMessage);
        }

        return head;
    }

    public static class MessageBuilder {
        private List<Message> messages = new ArrayList<>();
        private Message message;

        private MessageBuilder(String str) {
            message = new Message(str);
            messages.add(message);
        }

        public static MessageBuilder forMessage(String str) {
            return new MessageBuilder(str);
        }

        /**
         * This changes the current message to the specified and append it to the array.
         * So once you call this method, you lose the ability to build the previous
         * message.
         *
         * @param str string to append. Putting null in str does nothing.
         * @return the object itself.
         */
        public MessageBuilder append(String str) {
            if (str == null)
                return this;

            message = new Message(str);
            messages.add(message);
            return this;
        }

        public MessageBuilder withClickOpenUrl(String value) {
            message.resetClick();
            message.click_OpenUrl = value;
            return this;
        }

        public MessageBuilder withClickOpenFile(String value) {
            message.resetClick();
            message.click_OpenFile = value;
            return this;
        }

        public MessageBuilder withClickRunCommand(String value) {
            message.resetClick();
            message.click_RunCommand = value;
            return this;
        }

        public MessageBuilder withClickSuggestCommand(String value) {
            message.resetClick();
            message.click_SuggestCommand = value;
            return this;
        }

        public MessageBuilder withHoverShowText(String value) {
            message.resetHover();
            message.hover_ShowText = value;
            return this;
        }

        public MessageBuilder withHoverShowAchievement(String value) {
            message.resetHover();
            message.hover_ShowAchievement = value;
            return this;
        }

        public MessageBuilder withHoverShowItem(ItemStack value) {
            message.resetHover();
            message.hover_ShowItem = value;
            return this;
        }

        public JsonApiAPI.Message[] build() {
            return messages.toArray(new Message[0]);
        }
    }

    public static class Message {
        private String string;

        private String click_OpenUrl;
        private String click_OpenFile;
        private String click_RunCommand;
        private String click_SuggestCommand;

        private String hover_ShowText;
        private String hover_ShowAchievement;
        private ItemStack hover_ShowItem;

        private Message(String str) {
            this.string = str;
        }

        private void resetClick() {
            click_OpenUrl = null;
            click_OpenFile = null;
            click_RunCommand = null;
            click_SuggestCommand = null;
        }

        private void resetHover() {
            hover_ShowText = null;
            hover_ShowAchievement = null;
            hover_ShowItem = null;
        }
    }
}
