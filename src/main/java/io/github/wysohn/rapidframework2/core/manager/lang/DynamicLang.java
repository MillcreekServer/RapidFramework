package io.github.wysohn.rapidframework2.core.manager.lang;

public class DynamicLang {
    public final Lang lang;
    public final PreParseHandle handle;

    public DynamicLang(Lang lang, PreParseHandle handle) {
        this.lang = lang;
        this.handle = handle;
    }
}
