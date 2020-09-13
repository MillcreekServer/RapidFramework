package io.github.wysohn.rapidframework3.interfaces.language;

import io.github.wysohn.rapidframework3.core.language.ManagerLanguage;
import io.github.wysohn.rapidframework3.interfaces.ICommandSender;

@FunctionalInterface
public interface ILangParser {
    void onParse(ICommandSender sen, ManagerLanguage langman);
}