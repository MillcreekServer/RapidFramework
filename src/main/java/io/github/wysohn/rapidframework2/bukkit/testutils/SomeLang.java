package io.github.wysohn.rapidframework2.bukkit.testutils;

import io.github.wysohn.rapidframework2.core.manager.lang.Lang;

public enum SomeLang implements Lang {
    Blah("This is Blah");

    private final String[] eng;

    SomeLang(String... eng) {
        this.eng = eng;
    }

    @Override
    public String[] getEngDefault() {
        return eng;
    }
}