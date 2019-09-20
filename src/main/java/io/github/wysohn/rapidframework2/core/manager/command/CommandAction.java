package io.github.wysohn.rapidframework2.core.manager.command;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;

@FunctionalInterface
public interface CommandAction<Sender extends ICommandSender> {
    boolean execute(Sender sender, Arguments args);
}
