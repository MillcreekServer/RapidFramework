package io.github.wysohn.rapidframework3.core.interfaces.command;

import io.github.wysohn.rapidframework3.core.command.SubCommand;
import io.github.wysohn.rapidframework3.core.interfaces.ICommandSender;

@FunctionalInterface
public interface CommandAction {
    boolean execute(ICommandSender sender, SubCommand.Arguments args);
}
