package io.github.wysohn.rapidframework2.core.manager.lang;

import io.github.wysohn.rapidframework.pluginbase.PluginLanguage;
import io.github.wysohn.rapidframework.utils.files.JarUtil;
import io.github.wysohn.rapidframework2.core.interfaces.KeyValueStorage;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.PluginRuntime;
import io.github.wysohn.rapidframework2.core.main.PluginMain;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
        List<String> values = storage.get(convertToConfigName(lang.name()));
        if (values == null) {
            values = Stream.of(lang.getEngDefault()).collect(Collectors.toList());
        }

        return values;
    }

    private static String convertToConfigName(String langName) {
        return langName.replaceAll("_", ".");
    }
}
