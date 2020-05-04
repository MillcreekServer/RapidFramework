package io.github.wysohn.rapidframework2.core.manager.command;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.common.message.Message;
import io.github.wysohn.rapidframework2.core.manager.common.message.MessageBuilder;
import io.github.wysohn.rapidframework2.core.manager.lang.DefaultLangs;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ManagerCommand extends PluginMain.Manager {
    private final String mainCommand;
    private String defaultCommand = "help";
    private SubCommandMap commandMap;

    public ManagerCommand(int loadPriority, String mainCommand) {
        super(loadPriority);
        this.mainCommand = mainCommand;
    }

    @Override
    public void enable() throws Exception {
        this.commandMap = new SubCommandMap();

        initDefaultCommands();
    }

    private void initDefaultCommands() {
        addCommand(new SubCommand.Builder(main(), "reload")
                .withDescription(DefaultLangs.Command_Reload_Description)
                .addUsage(DefaultLangs.Command_Reload_Usage)
                .action(((sender, args) -> {
                    try {
                        main().load();
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
        return mainCommand;
    }

    public void addCommand(SubCommand cmd) {
        commandMap.register(cmd);
    }

    /**
     * Directly run the command.
     * Ex) /mainCommand args[0] args[1] ...
     * @param sender sender
     * @param args the arguments next to the main command
     * @return always true since we have our own way to show error messages.
     */
    public boolean runSubCommand(ICommandSender sender, String... args){
        return onCommand(sender, mainCommand, mainCommand, args);
    }

    /**
     * adapter method for command handling
     * @param sender sender
     * @param command the main command used. Command doesn't run if it does not match with 'mainCommand'
     * @param label the label of main command. Alias of the command or 'mainCommand'
     * @param args_in the arguments next to the command. Ex) /mainCommand args[0] args[1] ...
     * @return always true since we have our own way to show error messages.
     */
    public boolean onCommand(ICommandSender sender, String command, String label, String[] args_in) {
        if (!command.equals(mainCommand))
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

                dlang.handle.onParse(sender, main.lang());
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
            c.description.handle.onParse(sender, main.lang());
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
            dynamicLang.handle.onParse(sender, main.lang());
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
    public void showHelp(String label, final ICommandSender sender, int page) {
        List<SubCommand> list = commandMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(cmd -> sender.hasPermission(cmd.permission))
                .filter(cmd -> cmd.predicates.stream().allMatch(pred -> pred.test(sender)))
                .sorted((Comparator.comparing(cmd -> cmd.name)))
                .collect(Collectors.toList());

        main().lang().sendMessage(sender, DefaultLangs.General_Line);

        main().lang().sendMessage(sender, DefaultLangs.General_Header, ((sen, langman) -> {
            langman.addString(main().getPluginName());
        }));
        main().lang().sendRawMessage(sender, MessageBuilder.forMessage("").build());

        int max = main().conf().get("command.help.sentenceperpage")
                .map(Object::toString)
                .map(Integer::parseInt)
                .orElse(6);

        int remainder = list.size() % max;
        int divided = list.size() / max;
        int outof = remainder == 0 ? divided : divided + 1;

        page = Math.max(page, 0);
        page = Math.min(page, outof - 1);

        int index;
        for (index = page * max; index >= 0 && index < (page + 1) * max; index++) {
            if (index >= list.size())
                break;

            final SubCommand c = list.get(index);

            main().lang().sendRawMessage(sender, buildCommandDetail(main(), label, sender, c));
        }

        main().lang().sendMessage(sender, DefaultLangs.General_Line);

        if (main().lang().isJsonEnabled()) {
            String leftArrow = "&8[&a<---&8]";
            String home = "&8[&aHome&8]";
            String rightArrow = "&8[&a--->&8]";

            final String cmdPrev = "/" + mainCommand + " help " + page;
            final String cmdHome = "/" + mainCommand + " help";
            final String cmdNext = "/" + mainCommand + " help " + (page + 2);

            Message[] jsonPrev = MessageBuilder.forMessage(leftArrow)
                    .withHoverShowText(cmdPrev)
                    .withClickRunCommand(cmdPrev)
                    .build();
            Message[] jsonHome = MessageBuilder.forMessage(home)
                    .withHoverShowText(cmdHome)
                    .withClickRunCommand(cmdHome)
                    .build();
            Message[] jsonNext = MessageBuilder.forMessage(rightArrow)
                    .withHoverShowText(cmdNext)
                    .withClickRunCommand(cmdNext)
                    .build();

            Stream<Message> stream = Stream.of();
            stream = Stream.concat(stream, Arrays.stream(jsonPrev));
            stream = Stream.concat(stream, Arrays.stream(jsonHome));
            stream = Stream.concat(stream, Arrays.stream(jsonNext));

            main().lang().sendRawMessage(sender, stream.toArray(Message[]::new));
        } else {
            main().lang().sendMessage(sender, DefaultLangs.Command_Help_TypeHelpToSeeMore, ((sen, langman) ->
                    langman.addString(label)));
        }

        final int pageCopy = page + 1;
        main().lang().sendMessage(sender, DefaultLangs.Command_Help_PageDescription, ((sen, langman) ->
                langman.addInteger(pageCopy).addInteger(outof)));
        sender.sendMessageRaw("");
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
        if (!command.equals(mainCommand))
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
