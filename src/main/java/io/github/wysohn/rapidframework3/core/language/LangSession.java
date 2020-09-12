package io.github.wysohn.rapidframework3.core.language;

import io.github.wysohn.rapidframework3.core.interfaces.language.ILang;
import io.github.wysohn.rapidframework3.core.interfaces.language.ILangSession;
import io.github.wysohn.rapidframework3.core.interfaces.store.IKeyValueStorage;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LangSession implements ILangSession {
    private final IKeyValueStorage storage;

    @Inject
    public LangSession(IKeyValueStorage storage) {
        this.storage = storage;
    }

    public List<String> translate(ILang lang) {
        Optional<List<String>> optValues = storage.get(ILangSession.convertToConfigName(lang.name()));
        return optValues
                .map(ArrayList::new)
                .map(List.class::cast)
                .orElseGet(() -> Stream.of(lang.getEngDefault()).collect(Collectors.toList()));
    }

    public void fill(Collection<ILang> values) {
        values.forEach(lang -> Optional.of(lang).map(ILang::name)
                .map(ILangSession::convertToConfigName)
                .ifPresent(key -> {
                    Optional<List<String>> optValues = storage.get(key);
                    if (!optValues.isPresent()) {
                        storage.put(key, Arrays.asList(lang.getEngDefault()));
                    }
                }));
    }
}
