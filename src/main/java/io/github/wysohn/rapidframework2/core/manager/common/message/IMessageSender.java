package io.github.wysohn.rapidframework2.core.manager.common.message;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;

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

                    sender.sendMessageRaw(combined);

                    Optional.ofNullable(message[0].getHover_ShowText())
                            .map(text -> text.split("\n"))
                            .map(Stream::of)
                            .ifPresent(stringStream -> stringStream
                                    .map(s -> s.replaceAll("&", "\u00a7"))
                                    .forEach(hover -> sender.sendMessageRaw("  " + hover)));

                    Optional.ofNullable(message[0].getClick_RunCommand())
                            .map(text -> text.split("\n"))
                            .map(Stream::of)
                            .ifPresent(stringStream -> stringStream
                                    .map(s -> s.replaceAll("&", "\u00a7"))
                                    .forEach(run -> sender.sendMessageRaw("  " + run)));

                    Optional.ofNullable(message[0].getClick_SuggestCommand())
                            .map(text -> text.split("\n"))
                            .map(Stream::of)
                            .ifPresent(stringStream -> stringStream
                                    .map(s -> s.replaceAll("&", "\u00a7"))
                                    .forEach(suggest -> sender.sendMessageRaw("  " + suggest)));
                });
    }

    default void send(ICommandSender sender, Message[] message) {
        send(sender, message, false);
    }

    boolean isJsonEnabled();
}
