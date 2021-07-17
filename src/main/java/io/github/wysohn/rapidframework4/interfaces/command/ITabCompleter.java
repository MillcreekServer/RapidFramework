package io.github.wysohn.rapidframework4.interfaces.command;

import java.util.List;

public interface ITabCompleter {
    /**
     * Show candidate values that can be used to complete the argument.
     * It works after when a player provides at least one character (args.length > 0).
     * <p>
     * Example) <b>/cmd subcmd a</b>[abc, aabbcc, aaaa, ...]
     *
     * @param part partially finished argument
     * @return list of candidates; empty list; null (which shows online players)
     */
    List<String> getCandidates(String part);

    /**
     * Show hint. This only shows when a player just entered a space after an
     * argument before it and not a character is provided yet (args.length < 1).
     * <p>
     * Example) <b>/cmd subcmd </b>[somehint]
     *
     * @return String list with size 1; empty list; null (which shows online players)
     */
    default List<String> getHint() {
        return null;
    }
}
