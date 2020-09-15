package io.github.wysohn.rapidframework3.interfaces.command;

import io.github.wysohn.rapidframework3.core.command.SubCommand;
import io.github.wysohn.rapidframework3.interfaces.ICommandSender;

@FunctionalInterface
public interface CommandAction {
    boolean execute(ICommandSender sender, SubCommand.Arguments args);
}
