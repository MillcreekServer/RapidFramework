package io.github.wysohn.rapidframework2.core.manager.command;

import io.github.wysohn.rapidframework2.core.manager.lang.Lang;

@SuppressWarnings("serial")
public class InvalidArgumentException extends Exception {
    final Lang lang;

    InvalidArgumentException(Lang lang) {
        super();
        this.lang = lang;
    }
}