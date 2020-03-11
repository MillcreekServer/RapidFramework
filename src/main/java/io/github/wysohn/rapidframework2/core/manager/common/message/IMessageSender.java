package io.github.wysohn.rapidframework2.core.manager.common.message;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public interface IMessageSender {
    default void send(ICommandSender sender, Message[] message) {
        Arrays.stream(message)
                .map(Message::getString)
                .filter(Objects::nonNull)
                .reduce((a, b) -> a + " " + b)
                .ifPresent(combined -> {
                    sender.sendMessageRaw(combined);

                    if (isJsonEnabled()) {
                        Optional.ofNullable(message[0].getHover_ShowText())
                                .map(text -> text.split("\n"))
                                .map(Stream::of)
                                .ifPresent(stringStream -> stringStream
                                        .forEach(hover -> sender.sendMessageRaw("  " + hover)));
                    }
                });
    }

    boolean isJsonEnabled();
}
