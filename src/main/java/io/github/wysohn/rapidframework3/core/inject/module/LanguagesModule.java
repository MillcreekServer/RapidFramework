package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;
import io.github.wysohn.rapidframework3.core.inject.factory.IStorageFactory;
import io.github.wysohn.rapidframework3.core.language.DefaultLangs;
import io.github.wysohn.rapidframework3.core.language.LangSession;
import io.github.wysohn.rapidframework3.core.main.ManagerConfig;
import io.github.wysohn.rapidframework3.interfaces.language.ILang;
import io.github.wysohn.rapidframework3.interfaces.language.ILangSession;
import io.github.wysohn.rapidframework3.interfaces.language.ILangSessionFactory;
import io.github.wysohn.rapidframework3.interfaces.message.IBroadcaster;
import io.github.wysohn.rapidframework3.interfaces.store.IKeyValueStorage;
import io.github.wysohn.rapidframework3.utils.CollectionHelper;
import io.github.wysohn.rapidframework3.utils.FileUtil;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class LanguagesModule extends AbstractModule {
    private final IBroadcaster broadcaster;
    private final ILang[] otherLangs;

    public LanguagesModule(IBroadcaster broadcaster,
                           ILang... otherLangs) {
        this.broadcaster = broadcaster;
        this.otherLangs = otherLangs;
    }

    @Provides
    Set<ILang> getLangs() {
        Set<ILang> langs = new HashSet<>(Arrays.asList(DefaultLangs.values().clone()));
        langs.addAll(Arrays.asList(otherLangs.clone()));
        return langs;
    }

    @Provides
    Locale getLocale() {
        return Locale.ENGLISH;
    }

    @Provides
    ILangSessionFactory getFactory(ManagerConfig config,
                                   @PluginDirectory File pluginDir,
                                   IStorageFactory storageFactory) {
        return new ILangSessionFactory() {
            @Override
            public Set<Locale> getLocales() {
                return config.get("language.locales")
                        .filter(List.class::isInstance)
                        .map(l -> (List<String>) l)
                        .map(Collection::stream)
                        .map(stream -> stream.filter(Objects::nonNull)
                                .map(Locale::forLanguageTag)
                                .collect(Collectors.toSet()))
                        .orElseGet(() -> CollectionHelper.set(Locale.ENGLISH));
            }

            @Override
            public ILangSession create(Locale locale) {
                IKeyValueStorage storage = storageFactory.create(FileUtil.join(pluginDir, "lang"),
                        locale.getLanguage() + ".yml");
                return new LangSession(storage);
            }
        };
    }

    @Provides
    IBroadcaster getBroadcaster() {
        return broadcaster;
    }
}
