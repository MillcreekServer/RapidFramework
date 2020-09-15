package io.github.wysohn.rapidframework3.core.message;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Message {
    String string;

    String click_OpenUrl;
    String click_OpenFile;
    String click_RunCommand;
    String click_SuggestCommand;

    String hover_ShowText;
    String hover_ShowAchievement;
    String hover_ShowItem;

    Message(String str) {
        this.string = str;
    }

    public void resetClick() {
        click_OpenUrl = null;
        click_OpenFile = null;
        click_RunCommand = null;
        click_SuggestCommand = null;
    }

    public void resetHover() {
        hover_ShowText = null;
        hover_ShowAchievement = null;
        hover_ShowItem = null;
    }

    public String getString() {
        return string;
    }

    public String getClick_OpenUrl() {
        return click_OpenUrl;
    }

    public String getClick_OpenFile() {
        return click_OpenFile;
    }

    public String getClick_RunCommand() {
        return click_RunCommand;
    }

    public String getClick_SuggestCommand() {
        return click_SuggestCommand;
    }

    public String getHover_ShowText() {
        return hover_ShowText;
    }

    public String getHover_ShowAchievement() {
        return hover_ShowAchievement;
    }

    public String getHover_ShowItem() {
        return hover_ShowItem;
    }

    public void setClick_OpenUrl(String click_OpenUrl) {
        this.click_OpenUrl = click_OpenUrl;
    }

    public void setClick_OpenFile(String click_OpenFile) {
        this.click_OpenFile = click_OpenFile;
    }

    public void setClick_RunCommand(String click_RunCommand) {
        this.click_RunCommand = click_RunCommand;
    }

    public void setClick_SuggestCommand(String click_SuggestCommand) {
        this.click_SuggestCommand = click_SuggestCommand;
    }

    public void setHover_ShowText(String hover_ShowText) {
        this.hover_ShowText = hover_ShowText;
    }

    public void setHover_ShowAchievement(String hover_ShowAchievement) {
        this.hover_ShowAchievement = hover_ShowAchievement;
    }

    public void setHover_ShowItem(String hover_ShowItem) {
        this.hover_ShowItem = hover_ShowItem;
    }

    public void colorize(Function<String, String> consumer) {
        this.string = Optional.ofNullable(this.string)
                .map(consumer)
                .orElse(null);

        this.click_OpenUrl = Optional.ofNullable(this.click_OpenUrl)
                .map(consumer)
                .orElse(null);
        this.click_OpenFile = Optional.ofNullable(this.click_OpenFile)
                .map(consumer)
                .orElse(null);
        this.click_RunCommand = Optional.ofNullable(this.click_RunCommand)
                .map(consumer)
                .orElse(null);
        this.click_SuggestCommand = Optional.ofNullable(this.click_SuggestCommand)
                .map(consumer)
                .orElse(null);

        this.hover_ShowText = Optional.ofNullable(this.hover_ShowText)
                .map(consumer)
                .orElse(null);
        this.hover_ShowAchievement = Optional.ofNullable(this.hover_ShowAchievement)
                .map(consumer)
                .orElse(null);
        this.hover_ShowItem = Optional.ofNullable(this.hover_ShowItem)
                .map(consumer)
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (!string.equals(message.string)) return false;
        if (!Objects.equals(click_OpenUrl, message.click_OpenUrl))
            return false;
        if (!Objects.equals(click_OpenFile, message.click_OpenFile))
            return false;
        if (!Objects.equals(click_RunCommand, message.click_RunCommand))
            return false;
        if (!Objects.equals(click_SuggestCommand, message.click_SuggestCommand))
            return false;
        if (!Objects.equals(hover_ShowText, message.hover_ShowText))
            return false;
        if (!Objects.equals(hover_ShowAchievement, message.hover_ShowAchievement))
            return false;
        return Objects.equals(hover_ShowItem, message.hover_ShowItem);
    }

    @Override
    public int hashCode() {
        int result = string.hashCode();
        result = 31 * result + (click_OpenUrl != null ? click_OpenUrl.hashCode() : 0);
        result = 31 * result + (click_OpenFile != null ? click_OpenFile.hashCode() : 0);
        result = 31 * result + (click_RunCommand != null ? click_RunCommand.hashCode() : 0);
        result = 31 * result + (click_SuggestCommand != null ? click_SuggestCommand.hashCode() : 0);
        result = 31 * result + (hover_ShowText != null ? hover_ShowText.hashCode() : 0);
        result = 31 * result + (hover_ShowAchievement != null ? hover_ShowAchievement.hashCode() : 0);
        result = 31 * result + (hover_ShowItem != null ? hover_ShowItem.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return string;
    }

    public static Message[] concat(Message[]... msg) {
        if (msg.length < 1)
            return MessageBuilder.empty();

        List<Message> msgs = new LinkedList<>();
        Arrays.stream(msg)
                .filter(Objects::nonNull)
                .forEach(arr -> Arrays.stream(arr)
                        .filter(Objects::nonNull)
                        .filter(m -> m.string.length() > 0)
                        .forEach(msgs::add));

        return msgs.toArray(new Message[0]);
    }

    public static String toRawString(Message[] msg) {
        return Arrays.stream(msg)
                .map(Message::getString)
                .collect(Collectors.joining(""));
    }

    public static Message[] join(String join, Message[] msg) {
        return Arrays.stream(msg)
                .collect(Collector.of(LinkedList<Message>::new, (result, m) -> {
                    if (m.string.trim().length() > 0) {
                        if (result.size() > 0)
                            result.add(new Message(join));
                        result.add(m);
                    }
                }, (left, right) -> {
                    left.addAll(right);
                    return left;
                }))
                .toArray(new Message[0]);
    }


}