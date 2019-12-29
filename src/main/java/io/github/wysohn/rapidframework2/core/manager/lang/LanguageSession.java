package io.github.wysohn.rapidframework2.core.manager.lang;

import io.github.wysohn.rapidframework2.core.interfaces.KeyValueStorage;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.PluginRuntime;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LanguageSession implements PluginRuntime {
    private final KeyValueStorage storage;

    public LanguageSession(KeyValueStorage storage) {
        this.storage = storage;
    }

    @Override
    public void enable() throws Exception {

    }

    @Override
    public void load() throws Exception {
        storage.reload();
    }

    @Override
    public void disable() throws Exception {

    }

    public List<String> translate(Lang lang){
        Optional<List<String>> optValues = storage.get(convertToConfigName(lang.name()));
        return optValues.orElseGet(()->Stream.of(lang.getEngDefault()).collect(Collectors.toList()));
    }

    private static String convertToConfigName(String langName) {
        return langName.replaceAll("_", ".");
    }
}
