package io.github.wysohn.rapidframework4.interfaces.chat;

import io.github.wysohn.rapidframework4.interfaces.ICommandSender;

public interface IPlaceholderSupport {
    String parse(ICommandSender sender, String str);
}
