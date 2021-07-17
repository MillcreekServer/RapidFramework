package io.github.wysohn.rapidframework4.interfaces.language;

import io.github.wysohn.rapidframework4.core.language.ManagerLanguage;
import io.github.wysohn.rapidframework4.interfaces.ICommandSender;

@FunctionalInterface
public interface ILangParser {
    void onParse(ICommandSender sen, ManagerLanguage langman);
}