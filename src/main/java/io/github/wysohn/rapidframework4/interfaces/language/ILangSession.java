package io.github.wysohn.rapidframework4.interfaces.language;

import java.util.Collection;
import java.util.List;

public interface ILangSession {
    List<String> translate(ILang lang);

    void fill(Collection<ILang> values);

    static String convertToConfigName(String langName) {
        return langName.replaceAll("_", ".");
    }
}
