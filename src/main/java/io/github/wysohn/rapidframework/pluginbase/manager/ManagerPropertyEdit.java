package io.github.wysohn.rapidframework.pluginbase.manager;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginLanguage.Language;
import io.github.wysohn.rapidframework.pluginbase.PluginManager;
import io.github.wysohn.rapidframework.pluginbase.manager.prompts.EditPrompt;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.Map;

public class ManagerPropertyEdit extends PluginManager<PluginBase> {

    public ManagerPropertyEdit(PluginBase base, int loadPriority) {
        super(base, loadPriority);
    }

    @Override
    protected void onEnable() throws Exception {

    }

    @Override
    protected void onDisable() throws Exception {

    }

    @Override
    protected void onReload() throws Exception {

    }

    /**
     * @param whom
     * @param title
     * @param property        This also can be retrieved via getSessionData of
     *                        ConversationContext named by
     *                        {@link #PROPERTY_SESSIONDATANAME}. (Ex.
     *                        ConversationContext.getSessionData(ManagerPropertyEdit.PROPERTY_SESSIONDATANAME))
     * @param abandonListener
     */
    public void startEdit(Player whom, String title, Map<Language, Object> property,
                          ConversationAbandonedListener abandonListener) {
        ConversationFactory factory = new ConversationFactory(base);

        EditPrompt prompt = new EditPrompt(base, Prompt.END_OF_CONVERSATION, title, property);
        Conversation conv = factory.thatExcludesNonPlayersWithMessage("Sorry, this is in-game only feature!")
                .withFirstPrompt(prompt).addConversationAbandonedListener(abandonListener).buildConversation(whom);
        conv.getContext().setSessionData(PROPERTY_SESSIONDATANAME, property);

        conv.begin();
    }

    public static final String PROPERTY_SESSIONDATANAME = "property";
}
