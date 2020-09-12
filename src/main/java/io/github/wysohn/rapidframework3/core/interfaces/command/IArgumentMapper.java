package io.github.wysohn.rapidframework3.core.interfaces.command;

import io.github.wysohn.rapidframework3.core.exceptions.InvalidArgumentException;

@FunctionalInterface
public interface IArgumentMapper<T> {
    /**
     * Try to convert the arg(String) to appropriate instance. Should throw
     * InvalidArgumentException with ManagerLanguage enum passed if cannot be converted.
     *
     * @param arg the current argument to convert
     * @return the converted value
     * @throws InvalidArgumentException the exception to be thrown if the given
     *                                  value cannot be converted. This ManagerLanguage can
     *                                  have one ${string} placeholder which will be
     *                                  automatically converted into the input
     *                                  argument. (e.g. if your error message is
     *                                  "invalid argument ${string}!" and the
     *                                  argument was "help", then it will be parsed
     *                                  into "invalid argument help!"
     */
    T apply(String arg) throws InvalidArgumentException;
}