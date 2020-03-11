package io.github.wysohn.rapidframework2.core.manager.chat;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.common.AbstractFileSession;
import io.github.wysohn.rapidframework2.core.manager.common.message.Message;
import io.github.wysohn.rapidframework2.core.manager.common.message.MessageBuilder;

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
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(str -> String.format(str, sender.getDisplayName()))
                .map(str -> placeholderSupport.parse(sender, str))
                .map(MessageBuilder::forMessage)
                .map(MessageBuilder::build)
                .orElse(MessageBuilder.forMessage("&8[&7"+sender.getDisplayName()+"&8]").build());

        Message[] formattedPrefixes = fileSession.get("Prefixes")
                .filter(Collection.class::isInstance)
                .map(Collection.class::cast)
                .map(Collection::stream)
                .map(sectionStream -> {
                    MessageBuilder builder = MessageBuilder.forMessage("");

                    sectionStream.filter(fileSession::isSection)
                            .forEach(section -> {
                                String value = getConfigValue(sender, section, "value")
                                        .orElse("?"); // at least value has to exist

                                String click_OpenUrl = getConfigValue(sender, section, "click_OpenUrl")
                                        .orElse(null);
                                String click_OpenFile = getConfigValue(sender, section, "click_OpenFile")
                                        .orElse(null);
                                String click_RunCommand = getConfigValue(sender, section, "click_RunCommand")
                                        .orElse(null);
                                String click_SuggestCommand = getConfigValue(sender, section, "click_SuggestCommand")
                                        .orElse(null);

                                String hover_ShowText = getConfigValue(sender, section, "hover_ShowText")
                                        .orElse(null);
                                String hover_ShowAchievement = getConfigValue(sender, section, "hover_ShowAchievement")
                                        .orElse(null);
                                String hover_ShowItem = getConfigValue(sender, section, "hover_ShowItem")
                                        .orElse(null);

                                builder.append(value)
                                        .withClickOpenUrl(click_OpenUrl)
                                        .withClickOpenFile(click_OpenFile)
                                        .withClickRunCommand(click_RunCommand)
                                        .withClickSuggestCommand(click_SuggestCommand)
                                        .withHoverShowText(hover_ShowText)
                                        .withHoverShowAchievement(hover_ShowAchievement)
                                        .withHoverShowItem(hover_ShowItem)
                                        .build();
                            });

                    return builder.build();
                }).orElse(MessageBuilder.empty());

        recipients.forEach(recipient -> main().lang().sendRawMessage(recipient, Message.concat(formattedPrefixes, formattedName)));
    }

    private Optional<String> getConfigValue(ICommandSender sender, Object section, String click_openUrl) {
        return fileSession.get(section, click_openUrl)
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(str -> placeholderSupport.parse(sender, str));
    }
}
