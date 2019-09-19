package io.github.wysohn.rapidframework2.manager.command;

import io.github.wysohn.rapidframework2.main.PluginMain;
import io.github.wysohn.rapidframework2.manager.Manager;

public final class ManagerCommand extends Manager {
    private final String mainCommand;
    private String defaultCommand = "help";
    private SubCommandMap commandMap;

    public ManagerCommand(PluginMain main, int loadPriority, String mainCommand) {
        super(main, loadPriority);
        this.mainCommand = mainCommand;
    }

    @Override
    public void enable() throws Exception {

    }

    @Override
    public void load() throws Exception {

    }

    @Override
    public void disable() throws Exception {

    }

    private void initDefaultCommands() {
        addCommand(SubCommand.Builder.forCommand("reload", base, 0).withPermission(adminPermission)
                .withDescription(DefaultLanguages.Command_Reload_Description)
                .addUsage(DefaultLanguages.Command_Reload_Usage).actOnPlayer(((sender, args) -> {
                    base.reloadPluginProcedures();
                    sender.sendMessage("Plugin is reloaded.");
                    return true;
                })).actOnConsole(((sender, args) -> {
                    base.reloadPluginProcedures();
                    sender.sendMessage("Plugin is reloaded.");
                    return true;
                })).withColor(ChatColor.LIGHT_PURPLE).create());

        addCommand(SubCommand.Builder.forCommand("status", base, -1).withPermission(adminPermission)
                .withDescription(DefaultLanguages.Command_Status_Description)
                .addUsage(DefaultLanguages.Command_Status_Usage, (lang, p) -> {
                    StringBuilder builder = new StringBuilder();
                    base.getPluginManagers().entrySet().stream().filter(entry -> entry.getValue().getInfo() != null)
                            .map(entry -> entry.getKey()).map(clazz -> clazz.getSimpleName())
                            .forEach(name -> builder.append(name + ' '));
                    lang.addString(builder.toString());
                }).actOnPlayer(((sender, args) -> {
                    String moduleName = args.get(0, null);
                    showStatus(sender, moduleName);
                    return true;
                })).actOnConsole(((sender, args) -> {
                    String moduleName = args.get(0, null);
                    showStatus(sender, moduleName);
                    return true;
                })).withColor(ChatColor.LIGHT_PURPLE).create());

//        addCommand(SubCommand.Builder.forCommand("import", base, 1)
//                .withPermission(adminPermission)
//                .withDescription(DefaultLanguages.Command_Import_Description)
//                .withUsage(DefaultLanguages.Command_Import_Usage)
//                .actOnConsole(((sender, args) -> {
//                    String fromName = args[0];
//
//                    Set<DatabaseTransferTask.TransferPair> pairs = new HashSet<DatabaseTransferTask.TransferPair>();
//
//                    for (Entry<Class<? extends PluginManager>, PluginManager> entry : base.getPluginManagers().entrySet()) {
//                        Set<String> allowedTypes = entry.getValue().getValidDBTypes();
//                        if (!allowedTypes.contains(fromName)) {
//                            base.getLogger().severe(entry.getKey().getSimpleName() + "@Invalid db type -- " + fromName);
//                            return false;
//                        }
//
//                        Set<DatabaseTransferTask.TransferPair> pair = entry.getValue().getTransferPair(fromName);
//                        if (pair != null)
//                            pairs.addAll(pair);
//                    }
//
//                    new Thread(new DatabaseTransferTask(base, pairs)).start();
//
//                    return true;
//                }))
//                .withColor(ChatColor.LIGHT_PURPLE)
//                .create());
    }

    private void showStatus(CommandSender sender, String moduleName) {
        base.sendMessage(sender, DefaultLanguages.General_Line);

        StringBuilder builder = new StringBuilder();
        base.APISupport.apis.keySet().forEach(name -> builder.append(name + " "));
        sender.sendMessage(
                ChatColor.AQUA + "Hooks" + ChatColor.DARK_GRAY + " : " + ChatColor.GRAY + builder.toString());
        sender.sendMessage("");

        if (moduleName == null) {
            base.getPluginManagers().forEach((clazz, manager) -> {
                if (manager.getInfo() != null) {
                    base.lang.addString(clazz.getSimpleName());
                    base.sendMessage(sender, DefaultLanguages.General_Header);
                    printStatusRecursive(sender, manager.getInfo(), 0);
                }
            });
        } else {
            PluginManager<?> manager = base.getManager(moduleName);
            if (manager == null) {
                sender.sendMessage(moduleName + " does not exist.");
                return;
            }

            base.lang.addString(moduleName);
            base.sendMessage(sender, DefaultLanguages.General_Header);
            printStatusRecursive(sender, manager.getInfo(), 0);
        }

        base.sendMessage(sender, DefaultLanguages.General_Line);
    }

    private void printStatusRecursive(CommandSender sender, Map<String, Object> map, int level) {
        if (map == null)
            return;

        String padding = StringUtil.repeat(" ", 2 * (level + 1));
        map.forEach((key, value) -> {
            if (value instanceof Map) {
                sender.sendMessage(padding + ChatColor.AQUA + key);
                printStatusRecursive(sender, (Map<String, Object>) value, level + 1);
            } else {
                sender.sendMessage(
                        padding + ChatColor.AQUA + key + ChatColor.DARK_GRAY + " : " + ChatColor.GRAY + value);
            }
        });
    }

    /**
     * Set default command to be used when a player run command without any
     * arguments. The default value is 'help,' but you may change it to other
     * command. This checks if the command actually exists, so <b> it should be
     * called after all the commands are registered. </b>
     *
     * @param command the command to be used as default
     */
    protected void setDefaultCommand(String command) {
        Validation.validate(command);
        Validation.validate(commandMap.getCommand(command), command + " is not found in command list!");

        this.defaultCommand = command;
    }

    public void addCommand(final SubCommand cmd) {
        commandAddQueue.add(new Runnable() {
            @Override
            public void run() {
                commandMap.register(null, cmd);
            }
        });

    }

    public boolean onCommand(CommandSender sender, Command arg0, String label, String[] args) {
        if (args.length == 0) {
            args = new String[]{defaultCommand};
        }

        if (args[0].equalsIgnoreCase("help")) {
            int page = 0;
            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]) - 1;
                } catch (NumberFormatException ex) {
                    base.lang.addString(args[1]);
                    base.sendMessage(sender, DefaultLanguages.General_NotInteger);
                    return true;
                }
            }

            this.showHelp(label, sender, page);

            return true;
        }

        String cmdLine = "";
        for (String str : args) {
            cmdLine += str + " ";
        }

        commandMap.dispatch(sender, cmdLine);

        return true;
    }

    /**
     * @param sender
     * @param page   0~size()
     */
    public void showHelp(String label, final CommandSender sender, int page) {
        List<SubCommand> list = new ArrayList<SubCommand>();
        for (Entry<String, SubCommand> entry : commandList.entrySet()) {
            SubCommand cmd = entry.getValue();
            if (!cmd.testPermissionSilent(sender)) {
                continue;
            }

            list.add(cmd);
        }

        base.sendMessage(sender, DefaultLanguages.General_Line);

        base.lang.addString(base.getDescription().getName());
        base.sendMessage(sender, DefaultLanguages.General_Header);
        sender.sendMessage("");

        int max = base.getPluginConfig().Command_Help_SentencePerPage;

        int remainder = list.size() % base.getPluginConfig().Command_Help_SentencePerPage;
        int divided = list.size() / base.getPluginConfig().Command_Help_SentencePerPage;
        int outof = remainder == 0 ? divided : divided + 1;

        page = Math.max(page, 0);
        page = Math.min(page, outof - 1);

        int index;
        for (index = page * max; index >= 0 && index < (page + 1) * max; index++) {
            if (index >= list.size())
                break;

            final SubCommand c = list.get(index);
            ChatColor color = ChatColor.GOLD;
            color = c.getCommandColor();

            SimpleEntry<Language, PreParseHandle> descPair = c.getDescription();
            descPair.getValue().onParse(base.lang, sender instanceof Player ? (Player) sender : null);
            String descValue = base.lang.parseFirstString(sender, descPair.getKey());

            // description
            base.lang.addString(label);
            base.lang.addString(color + c.toString());
            base.lang.addString(descValue);
            String description = base.lang.parseFirstString(sender, DefaultLanguages.Command_Format_Description);

            StringBuilder builder = new StringBuilder();

            // aliases
            StringBuilder builderAliases = new StringBuilder();
            for (String alias : c.getAliases()) {
                builderAliases.append(" " + alias);
            }
            if (builderAliases.length() > 0) {
                base.lang.addString(builderAliases.toString());
                builder.append(base.lang.parseFirstString(sender, DefaultLanguages.Command_Format_Aliases) + "\n");
            }

            // usages
            for (SimpleEntry<Language, PreParseHandle> langPair : c.getUsage()) {
                Language lang = langPair.getKey();
                langPair.getValue().onParse(base.lang, sender instanceof Player ? (Player) sender : null);
                base.lang.setCommand("/" + label + " " + c.getName());
                for (String parsedUsage : base.lang.parseStrings(sender, lang)) {
                    base.lang.addString(parsedUsage);
                    String usage = base.lang.parseFirstString(sender, DefaultLanguages.Command_Format_Usage);
                    builder.append(usage + "\n");
                }
            }

            if (sender instanceof Player && base.APISupport.isHooked("JsonApi")) {
                JsonApiAPI api = base.APISupport.getAPI("JsonApi");
                Message[] message = JsonApiAPI.MessageBuilder.forMessage(description)
                        .withHoverShowText(builder.toString())
                        .withClickRunCommand("/" + label + " " + c.toString() + " ").build();
                api.send((Player) sender, message);
            } else {
                sender.sendMessage(description);
                Stream.of(builder.toString().split("\n")).forEach(msg -> sender.sendMessage(msg));
            }
        }

        base.sendMessage(sender, DefaultLanguages.General_Line);

        // page
        base.lang.addInteger(page + 1);
        base.lang.addInteger(outof);
        base.sendMessage(sender, DefaultLanguages.Command_Help_PageDescription);

        // page navigation help
        if (base.APISupport.isHooked("JsonApi") && sender instanceof Player) {
            JsonApiAPI api = base.APISupport.getAPI("JsonApi");

            String leftArrow = ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "<---" + ChatColor.DARK_GRAY + "]";
            String home = ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Home" + ChatColor.DARK_GRAY + "]";
            String rightArrow = ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "--->" + ChatColor.DARK_GRAY + "]";

            final String previousCmd = "/" + mainCommand + " help " + (page);
            final String homeCmd = "/" + mainCommand + " help ";
            final String nextCmd = "/" + mainCommand + " help " + (page + 2);

            Message[] jsonPrevious = JsonApiAPI.MessageBuilder.forMessage(leftArrow).withHoverShowText(previousCmd)
                    .withClickRunCommand(previousCmd).build();
            Message[] jsonHome = JsonApiAPI.MessageBuilder.forMessage(home).withHoverShowText(homeCmd)
                    .withClickRunCommand(homeCmd).build();
            Message[] jsonNext = JsonApiAPI.MessageBuilder.forMessage(rightArrow).withHoverShowText(nextCmd)
                    .withClickRunCommand(nextCmd).build();

            Stream<Message> stream = Stream.of();
            stream = Stream.concat(stream, Arrays.stream(jsonPrevious));
            stream = Stream.concat(stream, Arrays.stream(jsonHome));
            stream = Stream.concat(stream, Arrays.stream(jsonNext));

            api.send((Player) sender, stream.toArray(Message[]::new));
        } else {
            base.lang.addString(label);
            base.sendMessage(sender, DefaultLanguages.Command_Help_TypeHelpToSeeMore);
        }
        sender.sendMessage(ChatColor.GRAY + "");
    }
}
