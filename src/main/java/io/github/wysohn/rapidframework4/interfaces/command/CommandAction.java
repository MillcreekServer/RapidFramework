package io.github.wysohn.rapidframework4.interfaces.command;

import io.github.wysohn.rapidframework4.core.command.SubCommand;
import io.github.wysohn.rapidframework4.interfaces.ICommandSender;

@FunctionalInterface
public interface CommandAction {
    boolean execute(ICommandSender sender, SubCommand.Arguments args);
}
