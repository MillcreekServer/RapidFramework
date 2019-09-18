package io.github.wysohn.rapidframework.pluginbase.manager.prompts;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginLanguage.Language;
import io.github.wysohn.rapidframework.pluginbase.language.DefaultLanguages;
import org.bukkit.conversations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PromptFacotry {
    static Prompt getEditPromptForValueType(PluginBase base, EditPromptBase parent, Language title,
                                            PromptFacotry.ValueChanger changer) {
        Object value = changer.getValue();

        if (value instanceof Number) {
            return new NumericPrompt() {

                @Override
                public String getPromptText(ConversationContext arg0) {
                    return base.lang.parseFirstString(arg0.getForWhom(), DefaultLanguages.General_Prompt_EnterNumber);
                }

                @Override
                protected Prompt acceptValidatedInput(ConversationContext arg0, Number arg1) {
                    changer.onChange(arg1);
                    return parent;
                }

            };
        } else if (value instanceof Boolean) {
            return new BooleanPrompt() {

                @Override
                public String getPromptText(ConversationContext arg0) {
                    return base.lang.parseFirstString(arg0.getForWhom(), DefaultLanguages.General_Prompt_EnterBoolean);
                }

                @Override
                protected Prompt acceptValidatedInput(ConversationContext arg0, boolean arg1) {
                    Boolean box = arg1;
                    changer.onChange(box);
                    return parent;
                }

            };
        } else if (value instanceof String) {
            return new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext arg0) {
                    return base.lang.parseFirstString(arg0.getForWhom(), DefaultLanguages.General_Prompt_EnterString);
                }

                @Override
                public Prompt acceptInput(ConversationContext arg0, String arg1) {
                    changer.onChange(arg1);
                    return parent;
                }

            };
        } else if (value instanceof Map) {
            return new EditPrompt(base, parent, title, (Map<Language, Object>) value);
        } else if (value instanceof List) {
            return new ListEditPrompt(base, parent, title, (List<String>) value);
        }

        return null;
    }

    static <K, V> List<Map.Entry<K, V>> mapToEntryList(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            list.add(entry);
        }
        return list;
    }

    interface ValueChanger {
        void onChange(Object newVal);

        Object getValue();
    }
}
