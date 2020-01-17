package io.github.wysohn.rapidframework2.core.manager.lang.message;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;

public interface IMessageSender{
    void send(ICommandSender sender, Message[] message);

    default boolean isJsonEnabled(){
        return false;
    }
}
