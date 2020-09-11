package io.github.wysohn.rapidframework3.core.language;

import io.github.wysohn.rapidframework3.core.interfaces.language.ILang;
import io.github.wysohn.rapidframework3.core.interfaces.language.ILangParser;

public class DynamicLang {
    public final ILang lang;
    public final ILangParser parser;

    public DynamicLang(ILang lang, ILangParser parser) {
        this.lang = lang;
        this.parser = parser;
    }

    public DynamicLang(ILang lang) {
        this(lang, (sen, langman) -> {
        });
    }
}
