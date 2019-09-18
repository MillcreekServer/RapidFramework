package io.github.wysohn.rapidframework.pluginbase.manager.prompts;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginLanguage.Language;
import io.github.wysohn.rapidframework.pluginbase.language.DefaultLanguages;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class IndexBasedPrompt<T> extends EditPromptBase {
    private final List<T> currentData;

    private int currentIndex = 0;

    public IndexBasedPrompt(PluginBase base, Prompt parent, Object title, List<T> list) {
        super(base, parent, title);
        this.currentData = list;
    }

    @Override
    public Prompt acceptInput(ConversationContext arg0, String arg1) {
        if (arg1.equals("done")) {
            return parent;
        } else if (arg1.length() > 0 && (arg1.charAt(0) == 'u' || arg1.charAt(0) == 'd')) {
            String[] split = arg1.split(" ", 2);
            if (split.length == 2) {
                int value = 0;
                try {
                    value = Integer.parseInt(split[1]);
                    if (value < 0)
                        value = 0;
                } catch (NumberFormatException e) {
                    return this;
                }

                if (arg1.charAt(0) == 'u')
                    value = -value;

                addToCurrentIndex(value);
            }
        }

        return this;
    }

    protected void addToCurrentIndex(int value) {
        if (value < 0) {
            currentIndex = Math.max(0, currentIndex + value);
        } else {
            currentIndex = Math.min(Math.max(0, currentData.size() - CONTENTSLINE_PER_PAGE), currentIndex + value);
        }
    }

    protected T get(int index) {
        if (!validateIndex(index))
            return null;

        return currentData.get(index);
    }

    protected void add(T value) {
        currentData.add(value);
    }

    protected void set(int index, T value) {
        if (!validateIndex(index))
            return;

        currentData.set(index, value);
    }

    protected void delete(int index) {
        if (!validateIndex(index))
            return;

        currentData.remove(index);
    }

    protected void swap(int index1, int index2) {
        if (!validateIndex(index1) || !validateIndex(index2))
            return;

        T temp = currentData.get(index1);
        currentData.set(index1, currentData.get(index2));
        currentData.set(index2, temp);
    }

    private boolean validateIndex(int index) {
        return !currentData.isEmpty() && index >= 0 && index < currentData.size();
    }

    @Override
    protected void print(Conversable conv) {
        super.print(conv);

        revalidateIndex();

        for (int i = 0; i < CONTENTSLINE_PER_PAGE; i++) {
            int realIndex = currentIndex + i;
            if (realIndex >= currentData.size())
                continue;

            T data = currentData.get(realIndex);
            if (data instanceof Map.Entry) {
                // highly likely the edit data
                Entry<Language, Object> entry = (Entry<Language, Object>) data;

                String translatedKey = base.lang.parseFirstString(conv, entry.getKey());
                String value = String.valueOf(entry.getValue());
                value = value.substring(0, Math.min(30, value.length()));

                base.lang.addString(translatedKey + "=" + value);
            } else {
                base.lang.addString(String.valueOf(data));
            }

            base.lang.addInteger(realIndex);
            conv.sendRawMessage(base.lang.parseFirstString(conv, DefaultLanguages.General_IndexBasedPrompt_ListFormat));
        }
        conv.sendRawMessage("");

        conv.sendRawMessage(currentIndex + " / " + currentData.size());
        conv.sendRawMessage("");

        conv.sendRawMessage(base.lang.parseFirstString(conv, DefaultLanguages.General_IndexBasedPrompt_UpDescription));
        conv.sendRawMessage(
                base.lang.parseFirstString(conv, DefaultLanguages.General_IndexBasedPrompt_DownDescription));
        conv.sendRawMessage(base.lang.parseFirstString(conv, DefaultLanguages.General_IndexBasedPrompt_Done));
        conv.sendRawMessage("");
    }

    private void revalidateIndex() {
        int validIndex = currentData.size() - 1 - CONTENTSLINE_PER_PAGE;
        if (currentIndex > validIndex)
            currentIndex = Math.max(0, validIndex);
    }
}
