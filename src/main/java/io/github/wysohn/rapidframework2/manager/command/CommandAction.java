package io.github.wysohn.rapidframework2.manager.command;

import io.github.wysohn.rapidframework2.interfaces.ICommandSender;

@FunctionalInterface
public interface CommandAction<Sender extends ICommandSender> {
    boolean execute(Sender sender, Arguments args);
}
