package io.github.wysohn.rapidframework3.core.interfaces;

import java.util.Locale;

public interface ICommandSender extends IPluginObject {
    default void sendMessageRaw(String... msg) {
        sendMessageRaw(false, msg);
    }

    void sendMessageRaw(boolean conversation, String... msg);

    Locale getLocale();

    /**
     * Check if this sender has permission. The permission is checked
     * based on OR operation, which means that it will return true if this sender has at least
     * one of the permissions provided.
     *
     * @param permissions permissions to check
     * @return true if has permission; false if this sender does not have any matching permission.
     */
    boolean hasPermission(String... permissions);

    String getDisplayName();

    /**
     * Check if conversation API is under progress. This is specific for Bukkit API for now.
     *
     * @return true if conversation is under progress; false otherwise.
     */
    boolean isConversing();
}
