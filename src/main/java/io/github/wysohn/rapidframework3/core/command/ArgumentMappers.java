package io.github.wysohn.rapidframework3.core.command;

import io.github.wysohn.rapidframework2.tools.StringUtil;
import io.github.wysohn.rapidframework3.core.exceptions.InvalidArgumentException;
import io.github.wysohn.rapidframework3.core.language.DefaultLangs;
import io.github.wysohn.rapidframework3.interfaces.command.IArgumentMapper;

public class ArgumentMappers {
    /**
     * map input to output without any conversion
     */
    public static final IArgumentMapper<String> IDENTITY = arg -> arg;
    /**
     * Same as IDENTITY except it checks whether the argument is null
     * or not following the pattern defined in {@link StringUtil#isValidName(String)}.
     */
    public static final IArgumentMapper<String> STRING = arg -> {
        if (arg == null || !StringUtil.isValidName(arg))
            throw new InvalidArgumentException(DefaultLangs.General_InvalidString, ((sen, langman) ->
                    langman.addString(arg)));

        return arg;
    };
    /**
     * map input to integer if possible
     */
    public static final IArgumentMapper<Integer> INTEGER = arg -> {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException ex) {
            throw new InvalidArgumentException(DefaultLangs.General_NotInteger, ((sen, langman) ->
                    langman.addString(arg)));
        }
    };
    /**
     * map input to double if possible
     */
    public static final IArgumentMapper<Double> DOUBLE = arg -> {
        try {
            return Double.parseDouble(arg);
        } catch (NumberFormatException ex) {
            throw new InvalidArgumentException(DefaultLangs.General_NotDecimal, ((sen, langman) ->
                    langman.addString(arg)));
        }
    };

    public static final <E extends Enum> IArgumentMapper<E> enumMapper(Class<? extends E> clazz, boolean toUpper) {
        return new EnumArgumentMapper<>(clazz, toUpper);
    }
}
