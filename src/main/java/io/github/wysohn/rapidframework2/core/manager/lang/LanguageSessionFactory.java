package io.github.wysohn.rapidframework2.core.manager.lang;

import io.github.wysohn.rapidframework2.core.main.PluginMain;

import java.util.*;
import java.util.stream.Collectors;

public interface LanguageSessionFactory {
    default Set<Locale> getLocales(PluginMain main) {
        Optional<List<String>> optLocales = main.conf().get("language.locales");
        return optLocales.map(Collection::stream)
                .map(stringStream -> stringStream.map(Locale::forLanguageTag).collect(Collectors.toSet()))
                .orElseGet(HashSet::new);
    }

    LanguageSession create(Locale locale);
}
