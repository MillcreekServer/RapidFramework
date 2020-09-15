package io.github.wysohn.rapidframework3.interfaces.chat;

import io.github.wysohn.rapidframework3.interfaces.ICommandSender;

public interface IPlaceholderSupport {
    String parse(ICommandSender sender, String str);
}
