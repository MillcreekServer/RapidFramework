/*******************************************************************************
 *     Copyright (C) 2017 wysohn
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package io.github.wysohn.rapidframework.pluginbase.language;

import io.github.wysohn.rapidframework.pluginbase.PluginLanguage.Language;

public enum DefaultLanguages implements Language {
    Plugin_NotEnabled("Plugin is not enabled. "),
    Plugin_SetEnableToTrue("Please check your setting at config.yml to make sure it's enabled."),
    Plugin_WillBeDisabled("Plugin will be disabled."),

    General_NotInteger("&c${string} is not an integer!"), General_NotDecimal("&c${string} is not a decimal!"),
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
    General_PlayerNotOnline("&cPlayer ${string} is not found in server!"),
    General_NoSuchPlayer("&cNo such player named ${string}!"),
    General_NoSuchCommand("&cNo such command ${string}!"),
    General_Allow("&aAllow"),
    General_Deny("&cDeny"),
    General_On("&aOn"),
    General_Off("&cOff"),
    General_NotABoolean("&c${string} is not a boolean!"),
    General_NotEnoughPermission("&cYou don't have enough permission!"),
    General_NothingOnYourHand("&cNothing on your hand!"),

    General_Prompt_EnterNumber("&7Enter the &6number &7below."),
    General_Prompt_EnterBoolean("&7Enter '&atrue&7' or '&cfalse&7' below."),
    General_Prompt_EnterString("&7Enter the new &6string&7 value below."),

    General_IndexBasedPrompt_ListFormat("&3${integer}&8. &7${string}"),
    General_IndexBasedPrompt_UpDescription("&du <num> &8- &7go up the list"),
    General_IndexBasedPrompt_DownDescription("&dd <num> &8- &7go down the list"),
    General_IndexBasedPrompt_Done("&ddone &8- &7finish editing"),

    General_ListEditPrompt_Add("&dadd <value> &8- &7add <value> to the list. Ex) add hoho"),
    General_ListEditPrompt_Del("&ddel <num> &8- &7delete data at <num> index. Ex) del 3"),

    General_PromptMain_EnterIndex("Enter the index of property to edit."),

    Economy_NotEnoughMoney("&cNot enough money! Required:[&6${double}&c]"),
    Economy_TookMoney("&aTook [&6${double}&a] from your account!"),

    Command_Format_Description("&6/${string} ${string} &5- &7${string}"),
    Command_Format_Aliases("&8&m &f &5Aliases&7: &a${string}"),
    Command_Format_Usage("&8&m &f &7${string}"),

    Command_Help_PageDescription("&6Page &7${integer}/${integer}"),
    Command_Help_TypeHelpToSeeMore("&6Type &6/${string} help &7<page> &6to see next pages."),
    Command_Help_Description("Show all commands and its desriptions of this plugin."),
    Command_Help_Usage("<page> for page to see."),

    Command_Reload_Description("reload config"),
    Command_Reload_Usage("&6${command} &8- &7reload config"),

    Command_Status_Description("Show status of the plugin."),
    Command_Status_Usage("&6${command} &8- &7show status of all modules",
            "&6${command} <module> &8- &7show status of 'module'", "&bModules &8: &d${string}"),

    Command_Import_Description("DB types: ${dbtype}"),
    Command_Import_Usage("<dbtype> to import data from <dbtype>."),

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

    private final String[] englishDefault;

    private DefaultLanguages(String... englishDefault) {
        this.englishDefault = englishDefault;
    }

    @Override
    public String[] getEngDefault() {
        return englishDefault;
    }
}
