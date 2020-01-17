package io.github.wysohn.rapidframework2.core.manager.lang;

import io.github.wysohn.rapidframework2.core.interfaces.KeyValueStorage;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.PluginRuntime;

import java.util.*;
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
        return optValues
                .map(ArrayList::new)
                .map(List.class::cast)
                .orElseGet(()->Stream.of(lang.getEngDefault()).collect(Collectors.toList()));
    }

    public void fill(Collection<Lang> values) {
        values.forEach(lang -> Optional.of(lang).map(Lang::name)
                .map(LanguageSession::convertToConfigName)
                .ifPresent(key -> {
                    Optional<List<String>> optValues = storage.get(key);
                    if (!optValues.isPresent()) {
                        storage.put(key, Arrays.asList(lang.getEngDefault()));
                    }
                }));

        try {
            storage.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String convertToConfigName(String langName) {
        return langName.replaceAll("_", ".");
    }
}
