package io.github.wysohn.rapidframework2.core.interfaces.entity;

import java.util.Locale;

public interface ICommandSender extends IPermissionHolder {
    void sendMessage(String... msg);

    Locale getLocale();
}
