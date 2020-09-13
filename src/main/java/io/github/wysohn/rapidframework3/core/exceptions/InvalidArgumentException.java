package io.github.wysohn.rapidframework3.core.exceptions;

import io.github.wysohn.rapidframework3.core.language.DynamicLang;
import io.github.wysohn.rapidframework3.interfaces.language.ILang;
import io.github.wysohn.rapidframework3.interfaces.language.ILangParser;

public class InvalidArgumentException extends Exception {
    private final DynamicLang dynamicLang;

    public InvalidArgumentException(ILang dynamicLang) {
        this(dynamicLang, (sen, langman) -> {
        });
    }

    public InvalidArgumentException(ILang dynamicLang, ILangParser parser) {
        super();
        this.dynamicLang = new DynamicLang(dynamicLang, parser);
    }

    public DynamicLang getDynamicLang() {
        return dynamicLang;
    }
}