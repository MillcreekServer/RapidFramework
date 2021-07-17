package io.github.wysohn.rapidframework4.core.language;

import java.util.UUID;

public class QueuedMessage {
    final UUID toUser;
    final String[] parsedMessage;

    private QueuedMessage() {
        toUser = null;
        parsedMessage = null;
    }

    public QueuedMessage(UUID toUser, String... parsedMessage) {
        this.toUser = toUser;
        this.parsedMessage = parsedMessage;
    }
}
