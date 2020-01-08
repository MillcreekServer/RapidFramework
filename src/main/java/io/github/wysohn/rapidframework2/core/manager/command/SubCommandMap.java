package io.github.wysohn.rapidframework2.core.manager.command;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.common.DoubleChecker;
import io.github.wysohn.rapidframework2.core.manager.lang.DefaultLangs;
import io.github.wysohn.rapidframework2.core.manager.lang.DynamicLang;

import java.util.*;
import java.util.function.Predicate;

class SubCommandMap {
    private final Map<String, SubCommand> commandList = new LinkedHashMap<>();
    private final Map<String, String> aliasMap = new HashMap<String, String>();

    private final Map<UUID, String> checking = new HashMap<>();
    private final DoubleChecker doubleChecker = new DoubleChecker();

    public void clearCommands() {
        commandList.clear();
        aliasMap.clear();
    }

    /**
     * @param main
     * @param sender
     * @param arg1
     * @return false if arg1 was 0 length String; true otherwise
     */
    public boolean dispatch(PluginMain main, ICommandSender sender, String arg1) {
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

        SubCommand command = commandList.get(cmd);

        if (command != null) {
            if (command.permission != null
                    && !sender.hasPermission(main.getRootPermission(), command.permission)) {
                main.lang().sendMessage(sender, DefaultLangs.General_NotEnoughPermission);
                return true;
            }

            for(Predicate<ICommandSender> predicate : command.predicates){
                if (!predicate.test(sender)){
                    return true;
                }
            }

            if (command.nArguments != -1 && command.nArguments != args.length) {
                showCommandDetails(main, sender, command);
                return true;
            }

            if (command.doubleCheck) {
                String checkingCommand = checking.remove(sender.getUuid());

                if (checkingCommand != null) {
                    doubleChecker.confirm(sender.getUuid());
                    return true;
                } else {
                    doubleChecker.reset(sender.getUuid());

                    checking.put(sender.getUuid(), command.name);
                    doubleChecker.init(sender.getUuid(), () -> {
                        if (!command.execute(sender, cmd, args)) {
                            showCommandDetails(main, sender, command);
                        }
                    }, () -> {
                        main.lang().sendMessage(sender, DefaultLangs.Command_DoubleCheck_Timeout, ((sen, langman) -> {
                            langman.addString(command.name);
                        }));
                    });

                    main.lang().sendMessage(sender, DefaultLangs.Command_DoubleCheck_Init);
                    return true;
                }
            } else {
                if (!command.execute(sender, cmd, args)) {
                    showCommandDetails(main, sender, command);
                }
            }

            return true;
        } else if (cmd.equals("")) {
            return false;
        } else {
            main.lang().sendMessage(sender, DefaultLangs.General_NoSuchCommand, ((sen, langman) -> {
                langman.addString(cmd);
            }));
            return true;
        }
    }

    private void showCommandDetails(PluginMain main, ICommandSender sender, SubCommand command) {
        DynamicLang descPair = command.description;
        if (descPair != null) {
            String descParsed = main.lang().parseFirst(descPair.lang);
            main.lang().sendMessage(sender, DefaultLangs.Command_Format_Description, ((sen, langman) -> {
                langman.addString(command.name);
                langman.addString(descParsed);
            }));
        }

        StringBuilder builder = new StringBuilder();
        Arrays.stream(command.aliases).forEach(builder::append);
        main.lang().sendMessage(sender, DefaultLangs.Command_Format_Aliases, ((sen, langman) -> {
            langman.addString(builder.toString());
        }));

        for (DynamicLang usageLang : command.usage) {
            String parsedUsage = main.lang().parseFirst(sender, usageLang.lang);
            main.lang().sendMessage(sender, DefaultLangs.Command_Format_Usage, ((sen, langman) -> {
                langman.addString(parsedUsage);
            }));
        }
    }

    public SubCommand getCommand(String arg0) {
        return commandList.get(arg0);
    }

    public Set<Map.Entry<String, SubCommand>> entrySet() {
        return commandList.entrySet();
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