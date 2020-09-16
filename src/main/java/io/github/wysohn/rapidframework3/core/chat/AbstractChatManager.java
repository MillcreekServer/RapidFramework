package io.github.wysohn.rapidframework3.core.chat;

import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;
import io.github.wysohn.rapidframework3.core.inject.factory.IStorageFactory;
import io.github.wysohn.rapidframework3.core.language.ManagerLanguage;
import io.github.wysohn.rapidframework3.core.main.Manager;
import io.github.wysohn.rapidframework3.core.message.Message;
import io.github.wysohn.rapidframework3.core.message.MessageBuilder;
import io.github.wysohn.rapidframework3.interfaces.ICommandSender;
import io.github.wysohn.rapidframework3.interfaces.chat.IPlaceholderSupport;
import io.github.wysohn.rapidframework3.interfaces.store.IKeyValueStorage;
import io.github.wysohn.rapidframework3.utils.Validation;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

public abstract class AbstractChatManager extends Manager {
    public static final String CHAT_YML = "chat.yml";

    private final IKeyValueStorage configStorage;
    private final IPlaceholderSupport placeholderSupport;
    private final ManagerLanguage lang;

    public AbstractChatManager(ManagerLanguage lang,
                               @PluginDirectory File pluginDir,
                               IStorageFactory storageFactory,
                               IPlaceholderSupport placeholderSupport) {
        this.lang = lang;
        this.configStorage = storageFactory.create(pluginDir, "chat.yml");
        this.placeholderSupport = placeholderSupport;
    }

    @Override
    public void enable() throws Exception {

    }

    @Override
    public void load() throws Exception {
        configStorage.reload();
    }

    @Override
    public void disable() throws Exception {

    }

    protected void onChat(ICommandSender sender, Collection<? extends ICommandSender> recipients, String message) {
        Message[] formattedName = configStorage.get("NameFormat")
                .filter(configStorage::isSection)
                .map(section -> {
                    MessageBuilder<?> builder = MessageBuilder.forMessage("");
                    buildMessage(sender, builder, section);
                    return builder.build();
                })
                .orElse(MessageBuilder.forMessage("&8[&7" + sender.getDisplayName() + "&8]").build());

        Message[] formattedPrefixes = configStorage.get("Prefixes")
                .filter(configStorage::isSection)
                .map(sections -> {
                    MessageBuilder<?> builder = MessageBuilder.forMessage("");
                    configStorage.getKeys(sections, false).forEach(key -> {
                        Object section = configStorage.get(key);
                        buildMessage(sender, builder, section);
                    });
                    return builder.build();
                }).orElse(MessageBuilder.empty());

        Message[] text = MessageBuilder.forMessage(" ")
                .append(message)
                .build();

        recipients.forEach(recipient ->
                lang.sendRawMessage(recipient, Message.concat(formattedPrefixes, formattedName, text)));
    }

    private void buildMessage(ICommandSender sender, MessageBuilder<?> builder, Object section) {
        Validation.validate(section, configStorage::isSection, section + " is not a section.");

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

    private Optional<String> getConfigValue(ICommandSender sender, Object section, String key) {
        return configStorage.get(section, key)
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(str -> placeholderSupport.parse(sender, str));
    }
}
