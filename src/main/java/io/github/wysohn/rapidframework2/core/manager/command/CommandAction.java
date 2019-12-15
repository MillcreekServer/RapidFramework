package io.github.wysohn.rapidframework2.core.manager.command;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;

@FunctionalInterface
public interface CommandAction {
    boolean execute(ICommandSender sender, SubCommand.Arguments args);
}
