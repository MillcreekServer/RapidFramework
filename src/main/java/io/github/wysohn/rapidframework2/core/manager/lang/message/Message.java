package io.github.wysohn.rapidframework2.core.manager.lang.message;

public class Message {
    String string;

    String click_OpenUrl;
    String click_OpenFile;
    String click_RunCommand;
    String click_SuggestCommand;

    String hover_ShowText;
    String hover_ShowAchievement;
    String hover_ShowItem;

    Message(String str) {
        this.string = str;
    }

    void resetClick() {
        click_OpenUrl = null;
        click_OpenFile = null;
        click_RunCommand = null;
        click_SuggestCommand = null;
    }

    void resetHover() {
        hover_ShowText = null;
        hover_ShowAchievement = null;
        hover_ShowItem = null;
    }

    public String getString() {
        return string;
    }

    public String getClick_OpenUrl() {
        return click_OpenUrl;
    }

    public String getClick_OpenFile() {
        return click_OpenFile;
    }

    public String getClick_RunCommand() {
        return click_RunCommand;
    }

    public String getClick_SuggestCommand() {
        return click_SuggestCommand;
    }

    public String getHover_ShowText() {
        return hover_ShowText;
    }

    public String getHover_ShowAchievement() {
        return hover_ShowAchievement;
    }

    public String getHover_ShowItem() {
        return hover_ShowItem;
    }
}