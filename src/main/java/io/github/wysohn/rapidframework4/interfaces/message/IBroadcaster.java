package io.github.wysohn.rapidframework4.interfaces.message;

import io.github.wysohn.rapidframework4.interfaces.ICommandSender;

import java.util.function.Consumer;

public interface IBroadcaster {
    void forEachSender(Consumer<ICommandSender> fn);
}
