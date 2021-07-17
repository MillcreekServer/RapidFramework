package io.github.wysohn.rapidframework4.interfaces;

import io.github.wysohn.rapidframework4.core.language.ManagerLanguage;
import io.github.wysohn.rapidframework4.core.message.MessageBuilder;
import io.github.wysohn.rapidframework4.interfaces.language.ILang;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface IPluginObject {
    UUID getUuid();

    default Map<Object, Object> properties(ManagerLanguage lang, ICommandSender sender) {
        Map<Object, Object> map = new HashMap<>();
        map.put(TO_STRING, MessageBuilder.forMessage(toString()).build());
        return map;
    }

    ILang TO_STRING = new ILang() {
        @Override
        public String[] getEngDefault() {
            return new String[]{"toString()"};
        }

        @Override
        public String name() {
            return "TO_STRING";
        }
    };
}
