package io.github.wysohn.rapidframework2.core.objects.message;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder<IS> {
    private List<Message<IS>> messages = new ArrayList<>();
    private Message<IS> message;

    private MessageBuilder(String str) {
        message = new Message<IS>(str);
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

        message = new Message<IS>(str);
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

    public MessageBuilder withHoverShowItem(IS value) {
        message.resetHover();
        message.hover_ShowItem = value;
        return this;
    }

    public Message[] build() {
        return messages.toArray(new Message[0]);
    }
}