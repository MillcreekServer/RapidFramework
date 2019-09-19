package io.github.wysohn.rapidframework2.manager.command;

import io.github.wysohn.rapidframework2.interfaces.ICommandSender;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class SubCommandMap {
    private final Map<String, SubCommand> commandList = new LinkedHashMap<String, SubCommand>();
    private final Map<String, String> aliasMap = new HashMap<String, String>();

    public void clearCommands() {
        commandList.clear();
        aliasMap.clear();
    }

    public boolean dispatch(ICommandSender sender, String arg1) {
        String[] split = arg1.split(" ");

        String cmd = split[0];
        if (aliasMap.containsKey(cmd)) {
            cmd = aliasMap.get(cmd);
        }

        String[] args = new String[split.length - 1];
        for (int i = 1; i < split.length; i++) {
            args[i - 1] = split[i];
        }

        SubCommand command = commandList.get(cmd);

        if (command != null) {
            if (command.nArguments != -1 && command.nArguments != args.length) {
                SimpleEntry<Language, PreParseHandle> descPair = command.getDescription();
                descPair.getValue().onParse(base.lang, sender instanceof Player ? (Player) sender : null);
                String desc = base.lang.parseFirstString(sender, descPair.getKey());
                base.lang.addString(mainCommand);
                base.lang.addString(color + command.toString());
                base.lang.addString(desc);
                base.sendMessage(sender, DefaultLanguages.Command_Format_Description);

                StringBuilder builder = new StringBuilder();
                for (String alias : command.getAliases()) {
                    builder.append(" " + alias);
                }
                base.lang.addString(builder.toString());
                base.sendMessage(sender, DefaultLanguages.Command_Format_Aliases);

                for (SimpleEntry<Language, PreParseHandle> langPair : command.getUsage()) {
                    Language lang = langPair.getKey();
                    langPair.getValue().onParse(base.lang, sender instanceof Player ? (Player) sender : null);
                    base.lang.setCommand("/" + mainCommand + " " + cmd);
                    String usage = base.lang.parseFirstString(sender, lang);
                    base.lang.addString(usage);
                    base.sendMessage(sender, DefaultLanguages.Command_Format_Usage);
                }
                return true;
            }

            if (command.getPermission() != null && !sender.hasPermission(adminPermission)
                    && !sender.hasPermission(command.getPermission())) {
                base.sendMessage(sender, DefaultLanguages.General_NotEnoughPermission);
                return true;
            }

            boolean result = command.execute(sender, cmd, args);
            if (!result) {
                ChatColor color = command.getCommandColor();
                SimpleEntry<Language, PreParseHandle> descPair = command.getDescription();
                descPair.getValue().onParse(base.lang, sender instanceof Player ? (Player) sender : null);
                String desc = base.lang.parseFirstString(sender, descPair.getKey());
                base.lang.addString(mainCommand);
                base.lang.addString(color + command.toString());
                base.lang.addString(desc);
                base.sendMessage(sender, DefaultLanguages.Command_Format_Description);

                StringBuilder builder = new StringBuilder();
                for (String alias : command.getAliases()) {
                    builder.append(" " + alias);
                }
                base.lang.addString(builder.toString());
                base.sendMessage(sender, DefaultLanguages.Command_Format_Aliases);

                for (SimpleEntry<Language, PreParseHandle> langPair : command.getUsage()) {
                    Language lang = langPair.getKey();
                    langPair.getValue().onParse(base.lang, sender instanceof Player ? (Player) sender : null);
                    String usage = base.lang.parseFirstString(sender, lang);
                    base.lang.addString(usage);
                    base.sendMessage(sender, DefaultLanguages.Command_Format_Usage);
                }
            }
            return true;
        } else if (cmd.equals("")) {
            return dispatch(sender, "help");
        } else {
            base.lang.addString(cmd);
            base.sendMessage(sender, DefaultLanguages.General_NoSuchCommand);
            return true;
        }
    }

    public SubCommand getCommand(String arg0) {
        return commandList.get(arg0);
    }

    public boolean register(String arg0, SubCommand arg1) {
        String[] aliases = arg1.getAliases();
        if (aliases != null) {
            for (String alias : aliases) {
                aliasMap.put(alias, arg1.getName());
            }
        }

        if (commandList.containsKey(arg1.getName())) {
            return false;
        }

        commandList.put(arg1.getName(), arg1);
        return true;
    }

    public boolean register(String arg0, String arg1, SubCommand arg2) {
        if (commandList.containsKey(arg0))
            return false;

        commandList.put(arg0, arg2);
        return true;
    }

    public void registerAll(String arg0, List<SubCommand> arg1) {
        for (SubCommand cmd : arg1) {
            register(null, cmd);
        }
    }

    public List<String> tabComplete(CommandSender arg0, String arg1) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

}