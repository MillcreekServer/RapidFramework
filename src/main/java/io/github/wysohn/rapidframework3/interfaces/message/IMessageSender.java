package io.github.wysohn.rapidframework3.interfaces.message;

import io.github.wysohn.rapidframework3.core.message.Message;
import io.github.wysohn.rapidframework3.interfaces.ICommandSender;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public interface IMessageSender {
    default void send(ICommandSender sender, Message[] message, boolean conversation) {
        Arrays.stream(message)
                .map(Message::getString)
                .filter(Objects::nonNull)
                .reduce((a, b) -> a + " " + b)
                .map(s -> s.replaceAll("&", "\u00a7")) // translate to section sign
                .ifPresent(combined -> {
                    // do not send if sender is engaged in conversation yet conversation is not set
                    if (sender.isConversing() && !conversation) {
                        return;
                    }

                    sender.sendMessageRaw(conversation, combined);

                    for (Message each : message) {
                        Optional.ofNullable(each.getHover_ShowText())
                                .map(text -> text.split("\n"))
                                .map(Stream::of)
                                .ifPresent(stringStream -> stringStream
                                        .map(s -> s.replaceAll("&", "\u00a7"))
                                        .forEach(hover -> sender.sendMessageRaw(conversation, "  " + hover)));

                        Optional.ofNullable(each.getClick_RunCommand())
                                .map(text -> text.split("\n"))
                                .map(Stream::of)
                                .ifPresent(stringStream -> stringStream
                                        .map(s -> s.replaceAll("&", "\u00a7"))
                                        .forEach(run -> sender.sendMessageRaw(conversation, "  " + run)));

                        Optional.ofNullable(each.getClick_SuggestCommand())
                                .map(text -> text.split("\n"))
                                .map(Stream::of)
                                .ifPresent(stringStream -> stringStream
                                        .map(s -> s.replaceAll("&", "\u00a7"))
                                        .forEach(suggest -> sender.sendMessageRaw(conversation, "  " + suggest)));
                    }
                });
    }

    default void send(ICommandSender sender, Message[] message) {
        send(sender, message, false);
    }

    boolean isJsonEnabled();

    void enqueueMessage(ICommandSender sender, String[] parsed);
}
