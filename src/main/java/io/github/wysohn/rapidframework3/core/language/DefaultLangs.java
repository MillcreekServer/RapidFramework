package io.github.wysohn.rapidframework3.core.language;

import io.github.wysohn.rapidframework3.interfaces.language.ILang;

public enum DefaultLangs implements ILang {
    Plugin_NotEnabled("Plugin is not enabled. "),
    Plugin_SetEnableToTrue("Please check your setting at config.yml to make sure it's enabled."),
    Plugin_WillBeDisabled("Plugin will be disabled."),

    General_String("${string}"),

    General_NotInteger("&c${string} is not an integer!"),
    General_NotDecimal("&c${string} is not a decimal!"),
    General_InvalidEnum("&c${string} is not a proper value!",
            "&9Values &8: ${string}"),
    General_OutOfBound("&c${string} is out of bound!"),
    General_OutOfBound_RangeIs("&crange: &6${integer} &7< &fvalue &7< &6${integer}"),
    General_Line("&6&m &6&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m "
            + "&7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m "
            + "&7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m "
            + "&7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m &7&m "
            + "&6&m &6&m "),
    General_Header("  &8&L<< &6${string} &8&L>>"),
    General_InvalidString("&c${string} is too long or contains invalid character!"),
    General_InvalidType("&c${string} is not a valid type!"),
    General_NoSuchPlayer("&cNo such player named ${string}!"),
    General_CantTargetSelf("&cCan't target yourself."),
    General_NoSuchCommand("&cNo such command ${string}!"),
    General_Allow("&aAllow"),
    General_Deny("&cDeny"),
    General_On("&aOn"),
    General_Off("&cOff"),
    General_NotABoolean("&c${string} is not a boolean!"),
    General_EnumNotMatching("&c${string} is not valid!",
            "&7Use &8: &6${string}"),
    General_NotEnoughPermission("&cYou don't have enough permission!"),
    General_NothingOnYourHand("&cNothing on your hand!"),

//    General_Prompt_EnterNumber("&7Enter the &6number &7below."),
//    General_Prompt_EnterBoolean("&7Enter '&atrue&7' or '&cfalse&7' below."),
//    General_Prompt_EnterString("&7Enter the new &6string&7 value below."),
//
//    General_IndexBasedPrompt_ListFormat("&3${integer}&8. &7${string}"),
//    General_IndexBasedPrompt_UpDescription("&du <num> &8- &7go up the list"),
//    General_IndexBasedPrompt_DownDescription("&dd <num> &8- &7go down the list"),
//    General_IndexBasedPrompt_Done("&ddone &8- &7finish editing"),
//
//    General_ListEditPrompt_Add("&dadd <value> &8- &7add <value> to the list. Ex) add hoho"),
//    General_ListEditPrompt_Del("&ddel <num> &8- &7delete data at <num> index. Ex) del 3"),

    General_Prompt_Int("&7Enter an integer in chat. (or type &dexit &7to exit.)"),
    General_Prompt_Double("&7Enter a decimal number in chat. (or type &dexit &7to exit.)"),
    General_Prompt_Confirm("&7Type &dyes &7in the chat to continue. (or type &dexit &7to exit.)"),

    Economy_NotEnoughMoney("&cNot enough money! Required:[&6${double}&c]"),
    Economy_TookMoney("&aTook [&6${double}&a] from your account!"),

    Command_DoubleCheck_Init("&7Are you sure to execute this command? &cBe careful since" +
            " it cannot be undone once processed! &7To proceed, &6run the same command again&7."),
    Command_DoubleCheck_Timeout("&7Command &6${string} &7is automatically cancelled."),

    Command_Format_Description("&6/${string} ${string} &5- &7${string}"),
    Command_Format_Aliases("&5Aliases&7: &a${string}"),
    Command_Format_Usage("&8>> &7${string}"),

    Command_Help_PageDescription("&6Page &7${integer}/${integer}"),
    Command_Help_TypeHelpToSeeMore("&7Type &d${string} &7to see other pages."),
    Command_Help_Description("Show all commands and its desriptions of this plugin."),
    Command_Help_Usage("<page> for page to see."),

    Command_Reload_Description("reload config"),
    Command_Reload_Usage("&6/... reload &8- &7reload config"),
    Command_Reload_Done("&aReload done."),

    Command_Status_Description("Show status of the plugin."),
    Command_Status_Usage("&6/... status &8- &7show status of all modules",
            "&6/... status <module> &8- &7show status of 'module'", "&bModules &8: &d${string}"),

    Command_Import_Description("DB types: ${dbtype}"),
    Command_Import_Usage("<dbtype> to import data from <dbtype>."),

    Manager_Group_NoSuchGroup("&cCouldn't find the group named &6${string}&c."),
    Manager_Group_AlreadyInGroup("&6${string} &7is already in group &6${string}&7."),
    Manager_Group_AddedToGroup("&6${string} &7is moved to group &6${string}&7."),

    Manager_VolatileTask_CanceledCauseMoved("&cTask is cancelled because you moved!"),

    Manager_TargetBlock_ReadyToClick(
            "&7Now &aclick &7the target block. You may just cancel it by &cshift + click &7any block."),
    Manager_TargetBlock_Canceled("&7Cancelled."),

    Manager_AreaSelection_DIFFERENTWORLD("&cPositions are in different worlds."),
    Manager_AreaSelection_COMPLETE("&dSmallest: ${string} , Largest: ${string}"),
    Manager_AreaSelection_LEFTSET("&aLeft ready"),
    Manager_AreaSelection_RIGHTSET("&aRight ready"),

    Manager_Arena_ArenaInfo_Format("&d${string} &8: &7${string}"),

    Manager_Structure_NotAValidBlock("&cThis structure can only be used on &6${string}&c!"),
    Manager_Structure_AlreadyThere("&7Another structure is already there."),

    Structure_Title("Title"),
    Structure_Trusts("Trusts"),
    Structure_PublicMode("PublicMode"),

    GUI_Button_PagedFrame_OutOfBound("&cNo more page to show!"),

    ;

    private final String[] eng;

    DefaultLangs(String... eng) {
        this.eng = eng;
    }

    @Override
    public String[] getEngDefault() {
        return eng;
    }
}
