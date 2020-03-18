package io.github.wysohn.rapidframework2.core.manager.command;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface TabCompleter {
    /**
     * Returns null. Spigot shows online players if returns null
     */
    TabCompleter NULL = part -> null;
    /**
     * returns empty list.
     */
    TabCompleter EMPTY = new TabCompleter() {
        @Override
        public List<String> getCandidates(String part) {
            return TabCompleter.list();
        }

        @Override
        public List<String> getHint() {
            return TabCompleter.list("?");
        }
    };
    /**
     * List of players. List will be null, and hint will be "&lt;player&gt;"
     */
    TabCompleter PLAYER = new TabCompleter() {
        @Override
        public List<String> getCandidates(String part) {
            return null;
        }

        @Override
        public List<String> getHint() {
            return TabCompleter.list("<player>");
        }
    };

    static List<String> list(String... strings) {
        return Arrays.stream(strings).collect(Collectors.toList());
    }

    /**
     * Show candidates values that can be used to complete the argument.
     * It works after when a player provides at least one character (args.length > 0).
     * <p>
     * Example) /cmd subcmd a[abc, aabbcc, aaaa, ...]
     *
     * @param part partially finished argument
     * @return list of candidates; empty list; null (which shows online players)
     */
    List<String> getCandidates(String part);

    /**
     * Show hint. This only shows when a player just entered a space after an
     * argument before and not a character is provided yet (args.length < 1).
     * <p>
     * Example) /cmd subcmd [somehint]
     *
     * @return String list with size 1; empty list; null (which shows online players)
     */
    default List<String> getHint() {
        return null;
    }
}
