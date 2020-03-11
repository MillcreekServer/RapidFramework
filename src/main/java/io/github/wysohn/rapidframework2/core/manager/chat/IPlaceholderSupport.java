package io.github.wysohn.rapidframework2.core.manager.chat;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;

public interface IPlaceholderSupport {
    String parse(ICommandSender sender, String str);
}
