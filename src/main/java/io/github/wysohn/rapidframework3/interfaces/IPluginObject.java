package io.github.wysohn.rapidframework3.interfaces;

import io.github.wysohn.rapidframework3.core.message.MessageBuilder;
import io.github.wysohn.rapidframework3.interfaces.language.ILang;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface IPluginObject {
    UUID getUuid();

    default Map<ILang, Object> properties() {
        Map<ILang, Object> map = new HashMap<>();
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
