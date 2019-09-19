package io.github.wysohn.rapidframework2.manager.lang;

import io.github.wysohn.rapidframework.pluginbase.PluginLanguage;

import java.util.*;

public class Language {
    private final Locale defaultLang;
    private final Set<Locale> supportLanguages;
    private final Map<Locale, LanguageFileSession> langFiles = new HashMap<>();
    private final Set<Lang> languages = new HashSet<>();

    private final Queue<Double> doub = new LinkedList<>();
    private final Queue<Integer> integer = new LinkedList<>();
    private final Queue<Long> llong = new LinkedList<>();
    private final Queue<String> string = new LinkedList<>();
    private final Queue<Boolean> bool = new LinkedList<>();

    public Language(Locale defaultLang, Set<Locale> supportLanguages) {
        this.defaultLang = defaultLang;
        this.supportLanguages = supportLanguages;
    }

    public Language(Set<Locale> supportLanguages){
        this(Locale.ENGLISH, supportLanguages);
    }

    /**
     * @param lang
     * @return true if there was no same Language already registered
     */
    public boolean registerLanguage(Lang lang) {
        return languages.add(lang);
    }


}
