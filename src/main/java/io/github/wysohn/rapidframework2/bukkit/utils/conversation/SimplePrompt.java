package io.github.wysohn.rapidframework2.bukkit.utils.conversation;

import io.github.wysohn.rapidframework2.bukkit.main.objects.BukkitCommandSender;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.lang.DynamicLang;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Validation;

public class SimplePrompt implements Prompt {
    private final PluginMain main;
    private final DynamicLang promptText;
    private final InputHandle inputHandle;

    private boolean blocksForInput = true;
    private Prompt next = END_OF_CONVERSATION;

    private SimplePrompt(PluginMain main, DynamicLang promptText, InputHandle inputHandle) {
        this.main = main;
        this.promptText = promptText;
        this.inputHandle = inputHandle;
    }

    public static SimplePrompt of(PluginMain main, DynamicLang promptText, InputHandle inputHandle) {
        return new SimplePrompt(main, promptText, inputHandle);
    }

    public static SimplePrompt of(PluginMain main, DynamicLang promptText, InputHandle inputHandle,
                                  boolean blocksForInput) {
        SimplePrompt simplePrompt = new SimplePrompt(main, promptText, inputHandle);
        simplePrompt.setBlocksForInput(blocksForInput);
        return simplePrompt;
    }

    public void setBlocksForInput(boolean blocksForInput) {
        this.blocksForInput = blocksForInput;
    }

    public void setNext(Prompt next) {
        Validation.assertNotNull(next);
        this.next = next;
    }

    @Override
    @NotNull
    public String getPromptText(@NotNull ConversationContext context) {
        Conversable conversable = context.getForWhom();
        Validation.validate(conversable, c -> c instanceof CommandSender, "Only available for CommandSender.");

        BukkitCommandSender sender = new BukkitCommandSender();
        sender.setSender((CommandSender) conversable);

        return main.lang().parseFirst(sender, promptText.lang, promptText.handle);
    }

    @Override
    public boolean blocksForInput(@NotNull ConversationContext context) {
        return blocksForInput;
    }

    @Override
    @Nullable
    public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        if ("exit".equalsIgnoreCase(input))
            return END_OF_CONVERSATION;

        if (inputHandle.acceptInput(context, input)) {
            return next;
        } else {
            return this;
        }
    }

    @FunctionalInterface
    public interface InputHandle {
        /**
         * @param context
         * @param input
         * @return true to continue; false to repeat current prompt
         */
        boolean acceptInput(@NotNull ConversationContext context, @Nullable String input);
    }
}
