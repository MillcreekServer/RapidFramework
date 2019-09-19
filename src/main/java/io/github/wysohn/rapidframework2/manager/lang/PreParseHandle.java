package io.github.wysohn.rapidframework2.manager.lang;

@FunctionalInterface
public interface PreParseHandle {
    void onParse(Enum<? extends Lang> lang);
}