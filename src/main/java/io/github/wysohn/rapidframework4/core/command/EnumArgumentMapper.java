package io.github.wysohn.rapidframework4.core.command;

import io.github.wysohn.rapidframework4.core.exceptions.InvalidArgumentException;
import io.github.wysohn.rapidframework4.core.language.DefaultLangs;
import io.github.wysohn.rapidframework4.interfaces.command.IArgumentMapper;

import java.util.Arrays;

public class EnumArgumentMapper<E extends Enum> implements IArgumentMapper<E> {
    private final Class<? extends E> clazz;
    private final boolean toUpper;

    public EnumArgumentMapper(Class<? extends E> clazz, boolean toUpper) {
        this.clazz = clazz;
        this.toUpper = toUpper;
    }

    @Override
    public E apply(String arg) throws InvalidArgumentException {
        try {
            return (E) Enum.valueOf(clazz, toUpper ? arg.toUpperCase() : arg);
        } catch (IllegalArgumentException ex) {
            throw new InvalidArgumentException(DefaultLangs.General_EnumNotMatching, ((sen, langman) ->
                    langman.addString(arg)
                            .addString(String.join("&8, ", Arrays.stream(clazz.getEnumConstants())
                                    .map(Enum::name)
                                    .map(name -> "&a" + name)
                                    .toArray(String[]::new)))));
        }
    }
}
