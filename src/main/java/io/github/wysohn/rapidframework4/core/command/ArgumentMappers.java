package io.github.wysohn.rapidframework4.core.command;

import io.github.wysohn.rapidframework4.core.exceptions.InvalidArgumentException;
import io.github.wysohn.rapidframework4.core.language.DefaultLangs;
import io.github.wysohn.rapidframework4.interfaces.command.IArgumentMapper;
import io.github.wysohn.rapidframework4.utils.StringUtil;

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
     * map input to integer if possible. Notice that the input can be '*'
     * to allow the user to ask for "all" instead of specifying the amount.
     * <p>
     * If '*' is provided, then the value will be mapped to -1.
     */
    public static final IArgumentMapper<Integer> INTEGER = arg -> {
        try {
            if ("*".equals(arg))
                return -1;

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
