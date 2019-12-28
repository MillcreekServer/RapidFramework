package io.github.wysohn.rapidframework2.core.manager.lang;

import io.github.wysohn.rapidframework2.core.main.PluginMain;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public interface LanguageSessionFactory {
    default Set<Locale> getLocales(PluginMain main){
        List<String> locales = main.conf().get("language.locales");
        return locales == null ? new HashSet<>()
                : locales.stream().map(Locale::forLanguageTag).collect(Collectors.toSet());
    }

    LanguageSession create(Locale locale);
}
