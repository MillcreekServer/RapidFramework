package io.github.wysohn.rapidframework3.interfaces.message;

import io.github.wysohn.rapidframework3.interfaces.ICommandSender;

import java.util.function.BiConsumer;

public interface IQueuedMessageConsumer extends BiConsumer<ICommandSender, String[]> {

}
