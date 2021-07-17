package io.github.wysohn.rapidframework4.core.message;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder<ItemStack> {
    protected final List<Message> messages = new ArrayList<>();
    protected Message message;

    protected MessageBuilder(String str) {
        append(str);
    }

    public static MessageBuilder<Void> forMessage(String str) {
        return new MessageBuilder<>(str);
    }

    /**
     * This changes the current message to the specified 'str' and append it to the array.
     * So once you call this method, you lose the ability to build the previous
     * message.
     *
     * @param str string to append. Putting null in str does nothing.
     * @return the object itself.
     */
    public MessageBuilder<ItemStack> append(String str) {
        if (str == null)
            return this;

        message = new Message(str);
        messages.add(message);
        return this;
    }

    public MessageBuilder<ItemStack> withClickOpenUrl(String value) {
        message.resetClick();
        message.click_OpenUrl = value;
        return this;
    }

    public MessageBuilder<ItemStack> withClickOpenFile(String value) {
        message.resetClick();
        message.click_OpenFile = value;
        return this;
    }

    public MessageBuilder<ItemStack> withClickRunCommand(String value) {
        message.resetClick();
        message.click_RunCommand = value;
        return this;
    }

    public MessageBuilder<ItemStack> withClickSuggestCommand(String value) {
        message.resetClick();
        message.click_SuggestCommand = value;
        return this;
    }

    public MessageBuilder<ItemStack> withHoverShowText(String value) {
        message.resetHover();
        message.hover_ShowText = value;
        return this;
    }

    public MessageBuilder<ItemStack> withHoverShowAchievement(String value) {
        message.resetHover();
        message.hover_ShowAchievement = value;
        return this;
    }

    public MessageBuilder<ItemStack> withHoverShowItem(String value) {
        message.resetHover();
        message.hover_ShowItem = value;
        return this;
    }

    public MessageBuilder<ItemStack> withHoverShowItem(ItemStack itemStack) {
        throw new RuntimeException("Not implemented.");
    }

    public Message[] build() {
        return messages.toArray(new Message[0]);
    }

    public static Message[] empty() {
        return new Message[]{new Message("")};
    }
}