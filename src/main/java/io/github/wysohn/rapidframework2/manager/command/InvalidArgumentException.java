package io.github.wysohn.rapidframework2.manager.command;

@SuppressWarnings("serial")
public static class InvalidArgumentException extends Exception {
    private final Language lang;

    public InvalidArgumentException(Language lang) {
        super();
        this.lang = lang;
    }
}