package io.github.wysohn.rapidframework3.core.interfaces.language;

import io.github.wysohn.rapidframework3.core.interfaces.ICommandSender;
import io.github.wysohn.rapidframework3.core.language.ManagerLanguage;

@FunctionalInterface
public interface ILangParser {
    void onParse(ICommandSender sen, ManagerLanguage langman);
}