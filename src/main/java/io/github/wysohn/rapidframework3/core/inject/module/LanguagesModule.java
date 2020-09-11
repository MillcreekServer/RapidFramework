package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.core.interfaces.language.ILang;
import io.github.wysohn.rapidframework3.core.language.DefaultLangs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LanguagesModule extends AbstractModule {
    private final ILang[] otherLangs;

    public LanguagesModule(ILang... otherLangs) {
        this.otherLangs = otherLangs;
    }

    @Provides
    Set<ILang> getLangs() {
        Set<ILang> langs = new HashSet<>(Arrays.asList(DefaultLangs.values().clone()));
        langs.addAll(Arrays.asList(otherLangs.clone()));
        return langs;
    }
}
