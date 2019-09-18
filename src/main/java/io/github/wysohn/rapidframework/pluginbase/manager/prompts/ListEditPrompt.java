package io.github.wysohn.rapidframework.pluginbase.manager.prompts;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginLanguage.Language;
import io.github.wysohn.rapidframework.pluginbase.language.DefaultLanguages;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

import java.util.List;

public class ListEditPrompt extends IndexBasedPrompt<String> {
    public ListEditPrompt(PluginBase base, Prompt parent, Language title, List<String> currentData) {
        super(base, parent, title, currentData);
    }

    public ListEditPrompt(PluginBase base, Language title, List<String> currentData) {
        super(base, END_OF_CONVERSATION, title, currentData);
    }

    @Override
    public Prompt acceptInput(ConversationContext arg0, String arg1) {
        Prompt next = super.acceptInput(arg0, arg1);

        if (next == this) {
            if (arg1.startsWith("add") && arg1.length() > 4) {
                String value = arg1.substring(3).trim();

                if (!value.isEmpty())
                    this.add(value);
            } else if (arg1.startsWith("del") && arg1.length() > 4) {
                String value = arg1.substring(3).trim();

                int index = -1;
                try {
                    index = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return this;
                }

                this.delete(index);
            }

            return this;
        }

        return next;
    }

    @Override
    public boolean blocksForInput(ConversationContext arg0) {
        return true;
    }

    @Override
    protected void print(Conversable conv) {
        super.print(conv);

        conv.sendRawMessage(base.lang.parseFirstString(conv, DefaultLanguages.General_ListEditPrompt_Add));
        conv.sendRawMessage(base.lang.parseFirstString(conv, DefaultLanguages.General_ListEditPrompt_Del));
    }
}
