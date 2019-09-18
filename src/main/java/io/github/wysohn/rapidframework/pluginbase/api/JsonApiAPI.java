package io.github.wysohn.rapidframework.pluginbase.api;

import com.coloredcarrot.jsonapi.impl.JsonClickEvent;
import com.coloredcarrot.jsonapi.impl.JsonHoverEvent;
import com.coloredcarrot.jsonapi.impl.JsonMsg;
import io.github.wysohn.rapidframework.pluginbase.PluginAPISupport.APISupport;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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
        JsonMsg jsonMessages = toJsonMessage(messages);
        jsonMessages.send(player);
    }

    public void send(Player[] player, JsonApiAPI.Message[] messages) {
        JsonMsg jsonMessages = toJsonMessage(messages);
        jsonMessages.send(player);
    }

    private JsonMsg toJsonMessage(JsonApiAPI.Message[] messages) {
        JsonMsg head = new JsonMsg();
        for (int i = 0; i < messages.length; i++) {
            JsonMsg jsonMessage = new JsonMsg();
            Message message = messages[i];

            jsonMessage.append(message.string);
            if (message.click_OpenFile != null) {
                jsonMessage.clickEvent(JsonClickEvent.openFile(message.click_OpenFile));
            }
            if (message.click_OpenUrl != null) {
                jsonMessage.clickEvent(JsonClickEvent.openUrl(message.click_OpenUrl));
            }
            if (message.click_RunCommand != null) {
                jsonMessage.clickEvent(JsonClickEvent.runCommand(message.click_RunCommand));
            }
            if (message.click_SuggestCommand != null) {
                jsonMessage.clickEvent(JsonClickEvent.suggestCommand(message.click_SuggestCommand));
            }
            if (message.hover_ShowText != null) {
                jsonMessage.hoverEvent(JsonHoverEvent.showText(message.hover_ShowText));
            }
            if (message.hover_ShowAchievement != null) {
                jsonMessage.hoverEvent(JsonHoverEvent.showAchievement(message.hover_ShowAchievement));
            }
            if (message.hover_ShowItem != null) {
                jsonMessage.hoverEvent(JsonHoverEvent.showItem(message.hover_ShowItem));
            }

            head.append(jsonMessage);
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
