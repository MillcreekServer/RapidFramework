package io.github.wysohn.rapidframework2.core.manager.lang;

public class DynamicLang {
    public final Enum<? extends Lang> lang;
    public final PreParseHandle handle;

    public DynamicLang(Enum<? extends Lang> lang, PreParseHandle handle) {
        this.lang = lang;
        this.handle = handle;
    }
}
