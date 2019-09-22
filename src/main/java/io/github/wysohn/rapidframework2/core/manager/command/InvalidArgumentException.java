package io.github.wysohn.rapidframework2.core.manager.command;

import io.github.wysohn.rapidframework2.core.manager.lang.Lang;

@SuppressWarnings("serial")
public class InvalidArgumentException extends Exception {
    final Enum<? extends Lang> lang;

    InvalidArgumentException(Enum<? extends Lang> lang) {
        super();
        this.lang = lang;
    }
}