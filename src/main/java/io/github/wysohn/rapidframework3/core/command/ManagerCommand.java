package io.github.wysohn.rapidframework3.core.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginCommands;
import io.github.wysohn.rapidframework3.core.language.DefaultLangs;
import io.github.wysohn.rapidframework3.core.language.Pagination;
import io.github.wysohn.rapidframework3.core.main.Manager;
import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.core.message.Message;
import io.github.wysohn.rapidframework3.core.message.MessageBuilder;
import io.github.wysohn.rapidframework3.interfaces.ICommandSender;
import io.github.wysohn.rapidframework3.utils.Validation;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public final class ManagerCommand extends Manager {
    private final String[] mainCommands;
    private final String defaultCommand = "help";
    private final Map<String, SubCommandMap> commandMaps = new HashMap<>();

    /**
     * Construct command manager.
     *
     * @param mainCommands commands (used after /). If more than one is provided, first command is served as 'main'
     */
    @Inject
    public ManagerCommand(PluginMain main,
                          @PluginCommands String[] mainCommands) {
        super(main);
        Validation.validate(mainCommands.length, val -> val > 0, "Must provide at least one command.");

        this.mainCommands = mainCommands;
        for (String mainCommand : mainCommands) {
            commandMaps.put(mainCommand, new SubCommandMap());
        }
    }

    @Override
    public void enable() throws Exception {
        initDefaultCommands();
    }

    private void initDefaultCommands() {
        addCommand(new SubCommand.Builder(main(), "reload")
                .withDescription(DefaultLangs.Command_Reload_Description)
                .addUsage(DefaultLangs.Command_Reload_Usage)
                .action(((sender, args) -> {
                    try {
                        main().load();
                        main().lang().sendMessage(sender, DefaultLangs.Command_Reload_Done);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    return true;
                }))
                .create());
    }

    @Override
    public void load() throws Exception {

    }

    @Override
    public void disable() throws Exception {

    }

    public String getMainCommand() {
        return mainCommands[0];
    }

    public void addCommand(String mainCommand, SubCommand cmd) {
        if (!commandMaps.containsKey(mainCommand))
            throw new RuntimeException(mainCommand + " is not a valid command.");

        commandMaps.get(mainCommand).register(cmd);
    }

    public void addCommand(SubCommand cmd) {
        addCommand(mainCommands[0], cmd);
    }

    /**
     * Directly run the command.
     * Ex) /mainCommand args[0] args[1] ...
     *
     * @param sender sender
     * @param args   the arguments next to the main command
     * @return always true since we have our own way to show error messages.
     */
    public boolean runSubCommand(ICommandSender sender, String... args) {
        return runSubCommand(sender, mainCommands[0], args);
    }

    /**
     * Directly run the command.
     * Ex) /mainCommand args[0] args[1] ...
     *
     * @param sender      sender
     * @param mainCommand specific command to be used
     * @param args        the arguments next to the main command
     * @return always true since we have our own way to show error messages.
     */
    public boolean runSubCommand(ICommandSender sender, String mainCommand, String[] args) {
        return onCommand(sender, mainCommand, mainCommand, args);
    }

    /**
     * adapter method for command handling
     *
     * @param sender  sender
     * @param command the main command used. Command doesn't run if it does not match with one of the
     *                registered mainCommands
     * @param label   the label of main command. Alias of the command or 'mainCommand'
     * @param args_in the arguments next to the command. Ex) /mainCommand args[0] args[1] ...
     * @return always true since we have our own way to show error messages.
     */
    public boolean onCommand(ICommandSender sender, String command, String label, String[] args_in) {
        SubCommandMap commandMap = commandMaps.get(command);
        if (commandMap == null)
            return true;

        final String[] args;
        if (args_in.length == 0) {
            args = new String[]{defaultCommand};
        } else {
            args = args_in;
        }

        StringBuilder cmdLine = new StringBuilder();
        for (String arg : args_in) {
            cmdLine.append(arg);
            cmdLine.append(' ');
        }

        if (!commandMap.dispatch(main(), sender, label, cmdLine.toString())) {
            int page = 0;
            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException ex) {
                    main().lang().sendMessage(sender, DefaultLangs.General_NotInteger, ((sen, langman) ->
                            langman.addString(args[1])));
                    return true;
                }
            }

            this.showHelp(label, sender, page - 1);

            return true;
        }

        return true;
    }

    static List<Message[]> buildSpecifications(PluginMain main, String label, ICommandSender sender, SubCommand c) {
        List<Message[]> messages = new ArrayList<>();

        if (c.specifications != null) {
            c.specifications.forEach((subpart, dlang) -> {
                MessageBuilder builder = MessageBuilder.forMessage("");

                builder.append("&8\u2514&d");
                String combined = "/" + label + " " + c.name + " " + subpart;
                builder.append(combined);
                builder.withHoverShowText(combined);
                builder.withClickSuggestCommand(combined);
                builder.append(" &f");

                dlang.parser.onParse(sender, main.lang());
                String descValue = main.lang().parseFirst(sender, dlang.lang);
                if (descValue.length() < 10) {
                    builder.append(descValue);
                } else {
                    builder.append(descValue.substring(0, 10) + "...");
                    builder.withHoverShowText(descValue);
                }

                messages.add(builder.build());
            });
        }

        return messages;
    }

    static Message[] buildCommandDetail(PluginMain main, String label, ICommandSender sender, SubCommand c) {
        if (c.description == null) {
            return MessageBuilder.empty();
        } else {
            // description
            c.description.parser.onParse(sender, main.lang());
            String descValue = main.lang().parseFirst(sender, c.description.lang);

            MessageBuilder messageBuilder = MessageBuilder.forMessage(main.lang().parseFirst(sender,
                    DefaultLangs.Command_Format_Description, ((sen, langman) -> {
                        langman.addString(label);
                        langman.addString(c.name);
                        langman.addString(descValue);
                    })))
                    .withClickSuggestCommand("/" + label + " " + c.name);

            String aliasAndUsage = buildAliasAndUsageString(main, sender, c);

            return messageBuilder.withHoverShowText(aliasAndUsage).build();
        }
    }

    static String buildAliasAndUsageString(PluginMain main, ICommandSender sender, SubCommand c) {
        StringBuilder builder = new StringBuilder();

        // aliases
        StringBuilder builderAliases = new StringBuilder();
        for (String alias : c.aliases) {
            builderAliases.append(' ');
            builderAliases.append(alias);
        }

        if (builderAliases.length() > 0) {
            builder.append(main.lang().parseFirst(sender, DefaultLangs.Command_Format_Aliases, ((sen, langman) -> {
                langman.addString(builderAliases.toString());
            })));
            builder.append('\n');
        }

        // usages
        c.usage.forEach(dynamicLang -> {
            dynamicLang.parser.onParse(sender, main.lang());
            String[] usageVals = main.lang().parse(sender, dynamicLang.lang);

            for (String usage : usageVals) {
                builder.append(main.lang().parseFirst(sender, DefaultLangs.Command_Format_Usage, ((sen, langman) ->
                        langman.addString(usage))));
                builder.append('\n');
            }
        });

        return builder.toString();
    }

    /**
     * @param sender
     * @param page   0~size()
     */
    public void showHelp(String mainCommand, String label, final ICommandSender sender, int page) {
        SubCommandMap commandMap = commandMaps.get(mainCommand);
        if (commandMap == null)
            throw new RuntimeException("Invalid main command: " + mainCommand);

        List<SubCommand> list = commandMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(cmd -> sender.hasPermission(cmd.permission))
                .sorted((Comparator.comparing(cmd -> cmd.name)))
                .collect(Collectors.toList());

        main().lang().sendRawMessage(sender, MessageBuilder.forMessage("").build());

        int max = main().conf().get("command.help.sentenceperpage")
                .map(Object::toString)
                .map(Integer::parseInt)
                .orElse(6);

        Pagination.list(main(), list, max, main().getPluginName(), "/" + mainCommand + " help")
                .show(sender, page, (s, c, i) -> buildCommandDetail(main(), label, s, c));
    }

    /**
     * @param sender
     * @param page   0~size()
     */
    public void showHelp(String label, final ICommandSender sender, int page) {
        showHelp(mainCommands[0], label, sender, page);
    }

    /**
     * adapter method for command handling
     *
     * @param sender  sender
     * @param command the main command used. Command doesn't run if it does not match with 'mainCommand'
     * @param alias   the label of main command. Alias of the command or 'mainCommand'
     * @param args_in the arguments next to the command. Ex) /mainCommand args[0] args[1] ...
     */
    public List<String> onTabComplete(ICommandSender sender, String command, String alias, String[] args_in) {
        SubCommandMap commandMap = commandMaps.get(command);
        if (commandMap == null)
            return new ArrayList<>();

        if (args_in.length < 2) {
            List<String> result = commandMap.entrySet().stream()
                    .map(Map.Entry::getValue)
                    .filter(cmd -> args_in.length == 1)
                    .filter(cmd -> args_in[args_in.length - 1].length() > 0)
                    .filter(cmd -> sender.hasPermission(cmd.permission))
                    .filter(cmd -> cmd.predicates.stream().allMatch(pred -> pred.test(sender)))
                    .filter(cmd -> cmd.name.startsWith(args_in[args_in.length - 1]))
                    .sorted((Comparator.comparing(cmd -> cmd.name)))
                    .map(cmd -> cmd.name)
                    .collect(Collectors.toList());

            if (result.isEmpty())
                result.add("...");

            return result;
        } else {
            // /cmd subcmd i0 i1 ...
            return commandMap.tabComplete(sender, args_in[0], args_in.length - 2, args_in[args_in.length - 1]);
        }
    }
}
