package io.github.wysohn.rapidframework2.core.objects.message;

public class Message<IS> {
    String string;

    String click_OpenUrl;
    String click_OpenFile;
    String click_RunCommand;
    String click_SuggestCommand;

    String hover_ShowText;
    String hover_ShowAchievement;
    IS hover_ShowItem;

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
}