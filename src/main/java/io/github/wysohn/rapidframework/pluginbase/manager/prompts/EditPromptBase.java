package io.github.wysohn.rapidframework.pluginbase.manager.prompts;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginLanguage.Language;
import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

public abstract class EditPromptBase implements Prompt {
    protected static final int CONTENTSLINE_PER_PAGE = 8;

    protected final PluginBase base;
    protected final Prompt parent;
    protected final Object title;

    public EditPromptBase(PluginBase base, Prompt parent, Object title) {
        super();
        this.base = base;
        this.parent = parent;
        this.title = title;
    }

    public PluginBase getBase() {
        return base;
    }

    public Prompt getParent() {
        return parent;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        if(!(arg0.getPlugin() instanceof PluginBase))
            return null;

        PluginBase base = (PluginBase) arg0.getPlugin();

        Bukkit.getScheduler().runTask(base, new Runnable() {
            @Override
            public void run() {
                Conversable conv = arg0.getForWhom();

                print(conv);
            }
        });
        return null;
    }

    protected void print(Conversable conv) {
        //clean up screen
        for(int i = 0; i < 15; i++)
            conv.sendRawMessage("");

        if(title instanceof Language)
            conv.sendRawMessage("<"+base.lang.parseFirstString(conv, (Language) title)+">");
        else
            conv.sendRawMessage("<"+title+">");
        conv.sendRawMessage("");
    }
}
