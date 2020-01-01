package io.github.wysohn.rapidframework2.core.manager.lang;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;

@FunctionalInterface
public interface PreParseHandle {
    void onParse(ICommandSender sen, ManagerLanguage langman);
}