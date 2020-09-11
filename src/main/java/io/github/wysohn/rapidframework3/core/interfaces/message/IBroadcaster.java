package io.github.wysohn.rapidframework3.core.interfaces.message;

import io.github.wysohn.rapidframework3.core.interfaces.ICommandSender;

import java.util.function.Consumer;

public interface IBroadcaster {
    void forEachSender(Consumer<ICommandSender> fn);
}
