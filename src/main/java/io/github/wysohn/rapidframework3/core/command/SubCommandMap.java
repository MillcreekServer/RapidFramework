package io.github.wysohn.rapidframework3.core.command;

import io.github.wysohn.rapidframework3.core.language.DefaultLangs;
import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.core.message.MessageBuilder;
import io.github.wysohn.rapidframework3.interfaces.ICommandSender;
import io.github.wysohn.rapidframework3.utils.DoubleChecker;

import java.util.*;
import java.util.function.Predicate;

class SubCommandMap {
    private final Map<String, SubCommand> commandList = new LinkedHashMap<>();
    private final Map<String, String> aliasMap = new HashMap<String, String>();

    private final Map<UUID, String> checking = new HashMap<>();
    private final DoubleChecker doubleChecker;

    public SubCommandMap() {
        doubleChecker = new DoubleChecker();
    }

    public SubCommandMap(DoubleChecker doubleChecker) {
        this.doubleChecker = doubleChecker;
    }

    public void clearCommands() {
        commandList.clear();
        aliasMap.clear();
    }

    /**
     * @param main
     * @param sender
     * @param label
     * @param arg1
     * @return false if arg1 was 0 length String; true otherwise
     */
    public boolean dispatch(PluginMain main, ICommandSender sender, String label, String arg1) {
        String[] split = arg1.split(" ");

        final String cmd;
        if (aliasMap.containsKey(split[0])) {
            cmd = aliasMap.get(split[0]);
        } else {
            cmd = split[0];
        }

        String[] args = new String[split.length - 1];
        System.arraycopy(split, 1, args, 0, split.length - 1);

        SubCommand command = commandList.get(cmd);

        if (command != null) {
            if (command.permission != null
                    && !sender.hasPermission(main.getRootPermission(), command.permission)) {
                main.lang().sendMessage(sender, DefaultLangs.General_NotEnoughPermission);
                return true;
            }

            for (Predicate<ICommandSender> predicate : command.predicates) {
                if (predicate instanceof RPredicate
                        && !((RPredicate<ICommandSender>) predicate).testWithMessage(sender)) {
                    return true;
                } else if (!predicate.test(sender)) {
                    return true;
                }
            }

            if (command.nArguments != -1 && command.nArguments != args.length) {
                sendCommandDetails(main, sender, label, command);
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
                        checking.remove(sender.getUuid());
                        if (!command.execute(sender, label, args)) {
                            sendCommandDetails(main, sender, label, command);
                        }
                    }, () -> {
                        checking.remove(sender.getUuid());
                        main.lang().sendMessage(sender, DefaultLangs.Command_DoubleCheck_Timeout, ((sen, langman) ->
                                langman.addString(command.name)));
                    });

                    main.lang().sendMessage(sender, DefaultLangs.Command_DoubleCheck_Init);
                    return true;
                }
            } else {
                if (!command.execute(sender, label, args)) {
                    sendCommandDetails(main, sender, label, command);
                }
            }

            return true;
        } else if (cmd.equals("") || cmd.equals("help")) {
            return false;
        } else {
            main.lang().sendMessage(sender, DefaultLangs.General_NoSuchCommand, ((sen, langman) -> {
                langman.addString(cmd);
            }));
            return true;
        }
    }

    private void sendCommandDetails(PluginMain main, ICommandSender sender, String label, SubCommand command) {
        main.lang().sendRawMessage(sender, MessageBuilder.empty());
        main.lang().sendRawMessage(sender, ManagerCommand.buildCommandDetail(main, label, sender, command));
        ManagerCommand.buildSpecifications(main, label, sender, command).forEach(message ->
                main.lang().sendRawMessage(sender, message));
    }

    public List<String> tabComplete(ICommandSender sender, String subCommand, int index, String partial) {
        SubCommand cmd = commandList.get(subCommand);
        if (cmd == null)
            return new ArrayList<>();

        if (partial.length() < 1) { // show hint if nothing is entered yet
            return cmd.tabHint(index);
        } else {
            return cmd.tabComplete(index, partial);
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