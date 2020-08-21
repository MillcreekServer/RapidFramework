package io.github.wysohn.rapidframework2.bukkit.manager.command;

import io.github.wysohn.rapidframework2.bukkit.utils.MaterialTrie;
import io.github.wysohn.rapidframework2.core.manager.command.TabCompleter;

import java.util.List;

public class BukkitTabCompleters {
    public static TabCompleter MATERIAL = new TabCompleter() {
        private final MaterialTrie materialTrie = MaterialTrie.singleton();

        @Override
        public List<String> getCandidates(String part) {
            return materialTrie.getAllStartsWith(part);
        }

        @Override
        public List<String> getHint() {
            return TabCompleter.list("<material>");
        }
    };
}
