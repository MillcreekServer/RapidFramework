package io.github.wysohn.rapidframework2.core.manager.command;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.interfaces.entity.IPermissionHolder;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.lang.DefaultLangs;
import io.github.wysohn.rapidframework2.core.manager.lang.DynamicLang;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class SubCommandMap<Sender extends ICommandSender> {
    private final Map<String, SubCommand<Sender>> commandList = new LinkedHashMap<>();
    private final Map<String, String> aliasMap = new HashMap<String, String>();

    public void clearCommands() {
        commandList.clear();
        aliasMap.clear();
    }

    public boolean dispatch(PluginMain main, Sender sender, String arg1) {
        String[] split = arg1.split(" ");

        final String cmd;
        if (aliasMap.containsKey(split[0])) {
            cmd = aliasMap.get(split[0]);
        } else {
            cmd = split[0];
        }

        String[] args = new String[split.length - 1];
        for (int i = 1; i < split.length; i++) {
            args[i - 1] = split[i];
        }

        SubCommand<Sender> command = commandList.get(cmd);

        if (command != null) {
            if (command.permission != null
                    && !sender.hasPermission(IPermissionHolder.CheckType.OR, main.getAdminPermission(), command.permission)) {
                main.lang().sendMessage(sender, DefaultLangs.General_NotEnoughPermission);
                return true;
            }

            if (command.nArguments != -1 && command.nArguments != args.length) {
                showCommandDetails(main, sender, command);
                return true;
            }

            boolean result = command.execute(sender, cmd, args);
            if (!result) {
                showCommandDetails(main, sender, command);
            }

            return true;
        } else if (cmd.equals("")) {
            return dispatch(main, sender, "help");
        } else {
            main.lang().sendMessage(sender, DefaultLangs.General_NoSuchCommand, (managerLanguage -> {
                managerLanguage.addString(cmd);
            }));
            return true;
        }
    }

    private void showCommandDetails(PluginMain main, Sender sender, SubCommand<Sender> command) {
        DynamicLang descPair = command.description;
        String descParsed = main.lang().parseFirst(sender, descPair.lang);
        main.lang().sendMessage(sender, DefaultLangs.Command_Format_Description, (managerLanguage -> {
            managerLanguage.addString(command.name);
            managerLanguage.addString(descParsed);
        }));

        StringBuilder builder = new StringBuilder();
        Arrays.stream(command.aliases).forEach(builder::append);
        main.lang().sendMessage(sender, DefaultLangs.Command_Format_Aliases, (managerLanguage -> {
            managerLanguage.addString(builder.toString());
        }));

        for (DynamicLang usageLang : command.usage) {
            String parsedUsage = main.lang().parseFirst(sender, usageLang.lang);
            main.lang().sendMessage(sender, DefaultLangs.Command_Format_Usage, (managerLanguage -> {
                managerLanguage.addString(parsedUsage);
            }));
        }
    }

    public SubCommand getCommand(String arg0) {
        return commandList.get(arg0);
    }

    public boolean register(SubCommand cmd) {
        String[] aliases = cmd.aliases;
        if (aliases != null) {
            for (String alias : aliases) {
                aliasMap.put(alias, cmd.name);
            }
        }

        if (commandList.containsKey(cmd.name)) {
            return false;
        }

        commandList.put(cmd.name, cmd);
        return true;
    }

}