package io.github.wysohn.rapidframework2.core.manager.command;

import io.github.wysohn.rapidframework2.core.manager.lang.DynamicLang;
import io.github.wysohn.rapidframework2.core.manager.lang.Lang;
import io.github.wysohn.rapidframework2.core.manager.lang.PreParseHandle;

@SuppressWarnings("serial")
public class InvalidArgumentException extends Exception {
    final DynamicLang dynamicLang;

    public InvalidArgumentException(Lang dynamicLang) {
        this(dynamicLang, (sen, langman) -> {});
    }

    public InvalidArgumentException(Lang dynamicLang, PreParseHandle handle) {
        super();
        this.dynamicLang = new DynamicLang(dynamicLang, handle);
    }
}