package io.github.wysohn.rapidframework3.core.command;

import io.github.wysohn.rapidframework3.interfaces.command.ITabCompleter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleters {
    /**
     * Returns null. Spigot shows online players if returns null
     */
    public static final ITabCompleter NULL = part -> null;
    /**
     * returns empty list.
     */
    public static final ITabCompleter EMPTY = new ITabCompleter() {
        @Override
        public List<String> getCandidates(String part) {
            return list();
        }

        @Override
        public List<String> getHint() {
            return list();
        }
    };
    /**
     * List of players. List will be null, and hint will be "&lt;player&gt;"
     */
    public static final ITabCompleter PLAYER = new ITabCompleter() {
        @Override
        public List<String> getCandidates(String part) {
            return null;
        }

        @Override
        public List<String> getHint() {
            return list("<player>");
        }
    };

    public static final List<String> list(String... strings) {
        return Arrays.stream(strings).collect(Collectors.toList());
    }

    public static final ITabCompleter hint(String hint) {
        return new ITabCompleter() {
            @Override
            public List<String> getCandidates(String part) {
                return list();
            }

            @Override
            public List<String> getHint() {
                return list(hint);
            }
        };
    }

    public static final ITabCompleter simple(String... arg) {
        return new ITabCompleter() {
            @Override
            public List<String> getCandidates(String part) {
                return Arrays.stream(arg)
                        .filter(val -> val.startsWith(part))
                        .collect(Collectors.toList());
            }

            @Override
            public List<String> getHint() {
                return Arrays.stream(arg)
                        .collect(Collectors.toList());
            }
        };
    }
}
