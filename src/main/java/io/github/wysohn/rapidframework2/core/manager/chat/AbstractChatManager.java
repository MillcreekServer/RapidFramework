package io.github.wysohn.rapidframework2.core.manager.chat;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.common.AbstractFileSession;
import io.github.wysohn.rapidframework2.core.manager.common.message.Message;
import io.github.wysohn.rapidframework2.core.manager.common.message.MessageBuilder;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.Optional;

public abstract class AbstractChatManager extends PluginMain.Manager {
    private final AbstractFileSession fileSession;
    private final IPlaceholderSupport placeholderSupport;

    public AbstractChatManager(int loadPriority, AbstractFileSession fileSession, IPlaceholderSupport placeholderSupport) {
        super(loadPriority);
        this.fileSession = fileSession;
        this.placeholderSupport = placeholderSupport;
    }

    @Override
    public void enable() throws Exception {

    }

    @Override
    public void load() throws Exception {
        fileSession.reload();
    }

    @Override
    public void disable() throws Exception {

    }

    protected void onChat(ICommandSender sender, Collection<? extends ICommandSender> recipients, String message) {
        Message[] formattedName = fileSession.get("NameFormat")
                .filter(ConfigurationSection.class::isInstance)
                .map(ConfigurationSection.class::cast)
                .map(section -> {
                    MessageBuilder builder = MessageBuilder.forMessage("");
                    buildMessage(sender, builder, section);
                    return builder.build();
                })
                .orElse(MessageBuilder.forMessage("&8[&7" + sender.getDisplayName() + "&8]").build());

        Message[] formattedPrefixes = fileSession.get("Prefixes")
                .filter(ConfigurationSection.class::isInstance)
                .map(ConfigurationSection.class::cast)
                .map(section -> section.getValues(false))
                .map(sections -> {
                    MessageBuilder builder = MessageBuilder.forMessage("");
                    sections.forEach((key, section) -> buildMessage(sender, builder, section));
                    return builder.build();
                }).orElse(MessageBuilder.empty());

        Message[] text = MessageBuilder.forMessage(" ")
                .append(message)
                .build();

        recipients.forEach(recipient ->
                main().lang().sendRawMessage(recipient, Message.concat(formattedPrefixes, formattedName, text)));
    }

    private void buildMessage(ICommandSender sender, MessageBuilder builder, Object section) {
        String value = getConfigValue(sender, section, "value")
                .map(str -> placeholderSupport.parse(sender, str))
                .orElse("?"); // at least value has to exist

        builder.append(value);

        getConfigValue(sender, section, "click_OpenUrl")
                .map(str -> placeholderSupport.parse(sender, str))
                .map(this::replaceNewLines)
                .ifPresent(builder::withClickOpenUrl);
        getConfigValue(sender, section, "click_OpenFile")
                .map(str -> placeholderSupport.parse(sender, str))
                .map(this::replaceNewLines)
                .ifPresent(builder::withClickOpenFile);
        getConfigValue(sender, section, "click_RunCommand")
                .map(str -> placeholderSupport.parse(sender, str))
                .map(this::replaceNewLines)
                .ifPresent(builder::withClickRunCommand);
        getConfigValue(sender, section, "click_SuggestCommand")
                .map(str -> placeholderSupport.parse(sender, str))
                .map(this::replaceNewLines)
                .ifPresent(builder::withClickSuggestCommand);

        getConfigValue(sender, section, "hover_ShowText")
                .map(str -> placeholderSupport.parse(sender, str))
                .map(this::replaceNewLines)
                .ifPresent(builder::withHoverShowText);
        getConfigValue(sender, section, "hover_ShowAchievement")
                .map(str -> placeholderSupport.parse(sender, str))
                .map(this::replaceNewLines)
                .ifPresent(builder::withHoverShowAchievement);
        getConfigValue(sender, section, "hover_ShowItem")
                .map(str -> placeholderSupport.parse(sender, str))
                .map(this::replaceNewLines)
                .ifPresent(builder::withHoverShowItem);
    }

    private String replaceNewLines(String s) {
        return s.replace("\\n", "\n");
    }

    private Optional<String> getConfigValue(ICommandSender sender, Object section, String click_openUrl) {
        return fileSession.get(section, click_openUrl)
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(str -> placeholderSupport.parse(sender, str));
    }
}
