package io.github.wysohn.rapidframework3.bukkit.manager.command;

import io.github.wysohn.rapidframework3.bukkit.utils.MaterialTrie;
import io.github.wysohn.rapidframework3.core.command.TabCompleters;
import io.github.wysohn.rapidframework3.interfaces.command.ITabCompleter;

import java.util.List;

public class BukkitTabCompleters {
    public static ITabCompleter MATERIAL = new ITabCompleter() {
        private final MaterialTrie materialTrie = MaterialTrie.singleton();

        @Override
        public List<String> getCandidates(String part) {
            return materialTrie.getAllStartsWith(part.toUpperCase());
        }

        @Override
        public List<String> getHint() {
            return TabCompleters.list("<material>");
        }
    };
}
