package io.github.wysohn.rapidframework3.bukkit.testutils;


import io.github.wysohn.rapidframework3.interfaces.language.ILang;

public enum SomeLang implements ILang {
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