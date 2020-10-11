package io.github.wysohn.rapidframework3.core.command;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginCommands;
import io.github.wysohn.rapidframework3.core.language.DefaultLangs;
import io.github.wysohn.rapidframework3.core.language.ManagerLanguage;
import io.github.wysohn.rapidframework3.core.main.Manager;
import io.github.wysohn.rapidframework3.core.main.ManagerConfig;
import io.github.wysohn.rapidframework3.core.message.Message;
import io.github.wysohn.rapidframework3.core.message.MessageBuilder;
import io.github.wysohn.rapidframework3.core.paging.Pagination;
import io.github.wysohn.rapidframework3.interfaces.ICommandSender;
import io.github.wysohn.rapidframework3.utils.Pair;
import io.github.wysohn.rapidframework3.utils.Validation;

import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public final class ManagerCommand extends Manager {
    private final String[] mainCommands;
    private final String defaultCommand = "help";
    private final ManagerLanguage lang;
    private final ManagerConfig config;
    private final String pluginName;
    private final Injector injector;

    private final Map<String, SubCommandMap> commandMaps = new HashMap<>();
    private final Map<String, String[]> linkedCommands = new HashMap<>();

    /**
     * Construct command manager.
     *
     * @param mainCommands commands (used after /). If more than one is provided, first command is served as 'main'
     */
    @Inject
    public ManagerCommand(ManagerLanguage lang,
                          ManagerConfig config,
                          @Named("pluginName") String pluginName,
                          @Named("rootPermission") String rootPermission,
                          @PluginCommands String[] mainCommands,
                          Injector injector) {
        Validation.validate(mainCommands.length, val -> val > 0, "Must provide at least one command.");

        this.lang = lang;
        this.config = config;
        this.pluginName = pluginName;
        this.mainCommands = mainCommands;
        this.injector = injector;

        for (String mainCommand : mainCommands) {
            commandMaps.put(mainCommand, new SubCommandMap(rootPermission));
        }
    }

    @Override
    public void enable() throws Exception {

    }

    private void initDefaultCommands() {

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

    /**
     * Link some mainCommand (which is specified in plugin.yml) to some other registered command.
     * Ex) if invoked linkMainCommand("someOtherCommand", "thisCommand", "tt"), then when a player
     * enters command "/someOtherCommand," it is equivalent as typing "/thisCommand tt". This is very
     * simple implementation, so it does not check if the target command is valid.
     *
     * @param mainCommand the mainCommand to hook.
     * @param target      target command to be executed when the provided mainCommand is executed.
     * @return true if linked; false if already linked.
     * @throws RuntimeException if 1. length of target is 0, 2. one or more element of target is null, or
     *                          3. the command (first element of target) is already linked.
     */
    public boolean linkMainCommand(String mainCommand, String... target) {
        Validation.validate(target, v -> v.length > 0, "At least one target must be provided.");
        Validation.assertArrayNotNull(target);
        Validation.validate(target[0], key -> !linkedCommands.containsKey(key),
                "Cannot link to the command that's already linked.");

        return linkedCommands.put(mainCommand, target) == null;
    }

    public void addCommand(String mainCommand, SubCommand.Builder cmd) {
        if (!commandMaps.containsKey(mainCommand))
            throw new RuntimeException(mainCommand + " is not a valid command.");

        commandMaps.get(mainCommand).register(cmd.create(injector));
        injector.injectMembers(cmd);
    }

    public void addCommand(SubCommand.Builder cmd) {
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
        Pair<SubCommandMap, String[]> commandMapPair = getSubCommandMap(command, args_in);

        if (commandMapPair == null)
            return true;

        SubCommandMap commandMap = commandMapPair.key;
        args_in = commandMapPair.value;

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

        if (!commandMap.dispatch(lang, sender, label, cmdLine.toString())) {
            int page = 0;
            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException ex) {
                    lang.sendMessage(sender, DefaultLangs.General_NotInteger, ((sen, langman) ->
                            langman.addString(args[1])));
                    return true;
                }
            }

            this.showHelp(label, sender, page - 1);

            return true;
        }

        return true;
    }

    static List<Message[]> buildSpecifications(ManagerLanguage lang, String label, ICommandSender sender, SubCommand c) {
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

                dlang.parser.onParse(sender, lang);
                String descValue = lang.parseFirst(sender, dlang.lang);
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

    static Message[] buildCommandDetail(ManagerLanguage lang, String label, ICommandSender sender, SubCommand c) {
        if (c.description == null) {
            return MessageBuilder.empty();
        } else {
            // description
            c.description.parser.onParse(sender, lang);
            String descValue = lang.parseFirst(sender, c.description.lang);

            MessageBuilder messageBuilder = MessageBuilder.forMessage(lang.parseFirst(sender,
                    DefaultLangs.Command_Format_Description, ((sen, langman) -> {
                        langman.addString(label);
                        langman.addString(c.name);
                        langman.addString(descValue);
                    })))
                    .withClickSuggestCommand("/" + label + " " + c.name);

            String aliasAndUsage = buildAliasAndUsageString(lang, sender, c);

            return messageBuilder.withHoverShowText(aliasAndUsage).build();
        }
    }

    static String buildAliasAndUsageString(ManagerLanguage lang, ICommandSender sender, SubCommand c) {
        StringBuilder builder = new StringBuilder();

        // aliases
        StringBuilder builderAliases = new StringBuilder();
        for (String alias : c.aliases) {
            builderAliases.append(' ');
            builderAliases.append(alias);
        }

        if (builderAliases.length() > 0) {
            builder.append(lang.parseFirst(sender, DefaultLangs.Command_Format_Aliases, ((sen, langman) -> {
                langman.addString(builderAliases.toString());
            })));
            builder.append('\n');
        }

        // usages
        c.usage.forEach(dynamicLang -> {
            dynamicLang.parser.onParse(sender, lang);
            String[] usageVals = lang.parse(sender, dynamicLang.lang);

            for (String usage : usageVals) {
                builder.append(lang.parseFirst(sender, DefaultLangs.Command_Format_Usage, ((sen, langman) ->
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
                .filter(cmd -> sender.hasPermission(cmd.getPermission()))
                .sorted((Comparator.comparing(cmd -> cmd.name)))
                .collect(Collectors.toList());

        lang.sendRawMessage(sender, MessageBuilder.forMessage("").build());

        int max = config.get("command.help.sentenceperpage")
                .map(Object::toString)
                .map(Integer::parseInt)
                .orElse(6);

        Pagination.list(lang, list, max, pluginName, "/" + mainCommand + " help")
                .show(sender, page, (s, c, i) -> buildCommandDetail(lang, label, s, c));
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
        Pair<SubCommandMap, String[]> commandMapPair = getSubCommandMap(command, args_in);

        if (commandMapPair == null)
            return new ArrayList<>();

        SubCommandMap commandMap = commandMapPair.key;
        args_in = commandMapPair.value;

        if (args_in.length < 2) {
            String[] fArgs_in = args_in;
            List<String> result = commandMap.entrySet().stream()
                    .map(Map.Entry::getValue)
                    .filter(cmd -> fArgs_in.length == 1)
                    .filter(cmd -> fArgs_in[fArgs_in.length - 1].length() > 0)
                    .filter(cmd -> sender.hasPermission(cmd.getPermission()))
                    .filter(cmd -> cmd.predicates.stream().allMatch(pred -> pred.test(sender)))
                    .filter(cmd -> cmd.name.startsWith(fArgs_in[fArgs_in.length - 1]))
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

    /**
     * Get SubCommandMap based on given command. It first search for 'linked command,' and if it exists,
     * command will be replaced accordance to value specified in {@link #linkMainCommand(String, String...)}
     *
     * @param command command that user enters
     * @param args_in arg that user enders
     * @return Pair of SubCommandMap and String[] (args). null if no such command is found.
     */
    private Pair<SubCommandMap, String[]> getSubCommandMap(String command, String[] args_in) {
        SubCommandMap commandMap = null;
        String[] linkedCommand = linkedCommands.get(command);
        // find linked command
        if (linkedCommand != null && linkedCommand.length > 0 && commandMaps.containsKey(linkedCommand[0])) {
            // replace with the linked command
            commandMap = commandMaps.get(linkedCommand[0]);

            // merge arguments (linkedCommand[1:] + args_in[0:])
            String[] temp_args_in = new String[linkedCommand.length - 1 + args_in.length];
            if (linkedCommand.length > 1)
                System.arraycopy(linkedCommand, 1, temp_args_in, 0, linkedCommand.length - 1);
            System.arraycopy(args_in, 0, temp_args_in, linkedCommand.length - 1, args_in.length);
            args_in = temp_args_in;
        }

        if (commandMap == null)
            commandMap = commandMaps.get(command);

        if (commandMap == null)
            return null;

        return Pair.of(commandMap, args_in);
    }
}
