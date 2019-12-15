package io.github.wysohn.rapidframework2.core.manager.command;

import io.github.wysohn.rapidframework2.core.manager.lang.DefaultLangs;
import util.StringUtil;

@FunctionalInterface
public interface ArgumentMapper {
    /**
     * map input to output without any conversion
     */
    static ArgumentMapper IDENTITY = arg -> arg;
    /**
     * Same as IDENTITY except it checks whether the argument is null
     * or not following the pattern defined in {@link StringUtil#isValidName(String)}.
     */
    static ArgumentMapper STRING = arg -> {
        if (arg == null || !StringUtil.isValidName(arg))
            throw new InvalidArgumentException(DefaultLangs.General_InvalidString);

        return arg;
    };
    /**
     * map input to integer if possible
     */
    static ArgumentMapper INTEGER = arg -> {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException ex) {
            throw new InvalidArgumentException(DefaultLangs.General_NotInteger);
        }
    };
    /**
     * map input to double if possible
     */
    static ArgumentMapper DOUBLE = arg -> {
        try {
            return Double.parseDouble(arg);
        } catch (NumberFormatException ex) {
            throw new InvalidArgumentException(DefaultLangs.General_NotDecimal);
        }
    };
//    /**
//     * map input to online player if possible
//     */
//    static ArgumentMapper PLAYER = arg -> {
//        Player player = Bukkit.getPlayer(arg);
//        if (player == null)
//            throw new InvalidArgumentException(DefaultLanguages.General_PlayerNotOnline);
//
//        return player;
//    };
//    /**
//     * map input to offline player if possible. The player
//     * should have joined the server at least once.
//     */
//    static ArgumentMapper OFFLINE_PLAYER = arg -> {
//        OfflinePlayer oplayer = Bukkit.getOfflinePlayer(arg);
//        if (oplayer == null || oplayer.getLastPlayed() < 1)
//            throw new InvalidArgumentException(DefaultLanguages.General_NoSuchPlayer);
//
//        return oplayer;
//    };

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
    Object apply(String arg) throws InvalidArgumentException;
}