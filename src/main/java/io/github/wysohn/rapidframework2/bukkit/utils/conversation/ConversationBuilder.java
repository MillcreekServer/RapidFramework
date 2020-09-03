package io.github.wysohn.rapidframework2.bukkit.utils.conversation;

import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.lang.DefaultLangs;
import io.github.wysohn.rapidframework2.core.manager.lang.DynamicLang;
import io.github.wysohn.rapidframework2.tools.Validation;
import io.github.wysohn.rapidframework2.tools.regex.CommonPatterns;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ConversationBuilder {
    private final PluginMain main;
    private final List<SimplePrompt> prompts = new LinkedList<>();

    private ConversationBuilder(PluginMain main) {
        this.main = main;
    }

    public static ConversationBuilder of(PluginMain main) {
        return new ConversationBuilder(main);
    }

    public ConversationBuilder appendCustom(DynamicLang prompt, SimplePrompt.InputHandle handle) {
        prompts.add(SimplePrompt.of(main, prompt, handle));
        return this;
    }

    public ConversationBuilder appendCustom(DynamicLang prompt, SimplePrompt.InputHandle handle, boolean blockForInput) {
        prompts.add(SimplePrompt.of(main, prompt, handle, blockForInput));
        return this;
    }

    public ConversationBuilder appendInt(BiFunction<ConversationContext, Integer, Boolean> fn) {
        prompts.add(SimplePrompt.of(main, new DynamicLang(DefaultLangs.General_Prompt_Int), ((context, input) -> {
            if (input == null)
                return false;

            if (!CommonPatterns.INTEGER.matcher(input).matches())
                return false;

            return fn.apply(context, Integer.parseInt(input));
        })));
        return this;
    }

    public ConversationBuilder appendDouble(BiFunction<ConversationContext, Double, Boolean> fn) {
        prompts.add(SimplePrompt.of(main, new DynamicLang(DefaultLangs.General_Prompt_Double), ((context, input) -> {
            if (input == null)
                return false;

            if (!CommonPatterns.DOUBLE.matcher(input).matches())
                return false;

            return fn.apply(context, Double.parseDouble(input));
        })));
        return this;
    }

    public ConversationBuilder appendConfirm(Consumer<ConversationContext> consumer) {
        prompts.add(SimplePrompt.of(main, new DynamicLang(DefaultLangs.General_Prompt_Confirm), (context, input) -> {
            if ("yes".equalsIgnoreCase(input)) {
                consumer.accept(context);
                return true;
            } else {
                return false;
            }
        }));
        return this;
    }

    public ConversationBuilder doTask(Consumer<ConversationContext> consumer) {
        prompts.add(SimplePrompt.of(main, null, (context, input) -> {
            consumer.accept(context);
            return true;
        }, false));
        return this;
    }

    public Conversation build(Conversable forWhom) {
        Validation.validate(prompts, l -> l.size() > 0, "At least one Prompt must exist.");

        Iterator<SimplePrompt> iter = prompts.iterator();

        SimplePrompt first = iter.next();
        SimplePrompt current = first;
        while (iter.hasNext()) {
            SimplePrompt next = iter.next();
            current.setNext(next);
            current = next;
        }

        return new Conversation(main.getBridge().getPlatform(), forWhom, first);
    }
}
