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
package io.github.wysohn.rapidframework.pluginbase;

import io.github.wysohn.rapidframework.database.tasks.DatabaseTransferTask;
import io.github.wysohn.rapidframework.pluginbase.PluginLanguage.Language;
import io.github.wysohn.rapidframework.pluginbase.PluginLanguage.PreParseHandle;
import io.github.wysohn.rapidframework.pluginbase.api.JsonApiAPI;
import io.github.wysohn.rapidframework.pluginbase.api.JsonApiAPI.Message;
import io.github.wysohn.rapidframework.pluginbase.commands.SubCommand;
import io.github.wysohn.rapidframework.pluginbase.language.DefaultLanguages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.stream.Stream;

public final class PluginCommandExecutor implements PluginProcedure {
    private PluginBase base;

    public final String mainCommand;
    public final String adminPermission;

    private SubCommandMap commandMap;

    private final Queue<Runnable> commandAddQueue = new LinkedList<Runnable>();

    protected PluginCommandExecutor(String mainCommand, String adminPermission) {
        this.mainCommand = mainCommand;
        this.adminPermission = adminPermission;
    }

    @Override
    public void onEnable(PluginBase base) throws Exception {
        this.base = base;
        this.commandMap = new SubCommandMap();

        initDefaultCommands();

        while (!commandAddQueue.isEmpty()) {
            Runnable run = commandAddQueue.poll();

            run.run();
        }
    }

    @Override
    public void onDisable(PluginBase base) throws Exception {

    }

    @Override
    public void onReload(PluginBase base) throws Exception {

    }

    private void initDefaultCommands(){
        addCommand(SubCommand.Builder.forCommand("reload", base, 0)
                .withPermission(adminPermission)
                .withDescription(DefaultLanguages.Command_Reload_Description)
                .addUsage(DefaultLanguages.Command_Reload_Usage)
                .actOnPlayer(((sender, args) -> {
                    base.reloadPluginProcedures();
                    sender.sendMessage("Plugin is reloaded.");
                    return true;
                }))
                .actOnConsole(((sender, args) -> {
                    base.reloadPluginProcedures();
                    sender.sendMessage("Plugin is reloaded.");
                    return true;
                }))
                .withColor(ChatColor.LIGHT_PURPLE)
                .create());
        
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

    public void addCommand(final SubCommand cmd) {
        commandAddQueue.add(new Runnable() {
            @Override
            public void run() {
                commandMap.register(null, cmd);
            }
        });

    }

    public boolean onCommand(CommandSender sender, Command arg0, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
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
     *
     * @param sender
     * @param page
     *            0~size()
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
            
            //description
            base.lang.addString(label);
            base.lang.addString(color + c.toString());
            base.lang.addString(descValue);
            String description = base.lang.parseFirstString(sender, DefaultLanguages.Command_Format_Description);
            
            StringBuilder builder = new StringBuilder();

            //aliases
            StringBuilder builderAliases = new StringBuilder();
            for (String alias : c.getAliases()) {
                builderAliases.append(" " + alias);
            }
            if(builderAliases.length() > 0) {
                base.lang.addString(builderAliases.toString());
                builder.append(base.lang.parseFirstString(sender, DefaultLanguages.Command_Format_Aliases) + "\n");
            }

            //usages
            for (SimpleEntry<Language, PreParseHandle> langPair : c.getUsage()) {
            	Language lang = langPair.getKey();
            	langPair.getValue().onParse(base.lang, sender instanceof Player ? (Player) sender : null);
            	base.lang.setCommand("/"+label+" "+c.getName());
            	for(String parsedUsage : base.lang.parseStrings(sender, lang)) {
            		base.lang.addString(parsedUsage);
            		String usage = base.lang.parseFirstString(sender, DefaultLanguages.Command_Format_Usage);
            		builder.append(usage+"\n");
            	}
            }
            
            if (sender instanceof Player && base.APISupport.isHooked("JsonApi")) {
                JsonApiAPI api = base.APISupport.getAPI("JsonApi");
                Message[] message = JsonApiAPI.MessageBuilder
                        .forMessage(description)
                        .withHoverShowText(builder.toString())
                        .withClickRunCommand("/" + label + " " + c.toString() + " ")
                        .build();
                api.send((Player) sender, message);
            } else {
                sender.sendMessage(description);
                Stream.of(builder.toString().split("\n"))
                	.forEach(msg -> sender.sendMessage(msg));
            }
        }

        base.sendMessage(sender, DefaultLanguages.General_Line);

        //page
        base.lang.addInteger(page + 1);
        base.lang.addInteger(outof);
        base.sendMessage(sender, DefaultLanguages.Command_Help_PageDescription);

        //page navigation help
        if (base.APISupport.isHooked("JsonApi") && sender instanceof Player) {
            JsonApiAPI api = base.APISupport.getAPI("JsonApi");

            String leftArrow = ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "<---" + ChatColor.DARK_GRAY + "]";
            String home = ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Home" + ChatColor.DARK_GRAY + "]";
            String rightArrow = ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "--->" + ChatColor.DARK_GRAY + "]";

            final String previousCmd = "/" + mainCommand + " help " + (page);
            final String homeCmd = "/" + mainCommand + " help ";
            final String nextCmd = "/" + mainCommand + " help " + (page + 2);

            Message[] jsonPrevious = JsonApiAPI.MessageBuilder.forMessage(leftArrow)
                    .withHoverShowText(previousCmd)
                    .withClickRunCommand(previousCmd)
                    .build();
            Message[] jsonHome = JsonApiAPI.MessageBuilder.forMessage(home)
                    .withHoverShowText(homeCmd)
                    .withClickRunCommand(homeCmd)
                    .build();
            Message[] jsonNext = JsonApiAPI.MessageBuilder.forMessage(rightArrow)
                    .withHoverShowText(nextCmd)
                    .withClickRunCommand(nextCmd)
                    .build();

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

    private final Map<String, SubCommand> commandList = new LinkedHashMap<String, SubCommand>();
    private final Map<String, String> aliasMap = new HashMap<String, String>();

    private class SubCommandMap {
        public void clearCommands() {
            commandList.clear();
            aliasMap.clear();
        }

        public boolean dispatch(CommandSender sender, String arg1) throws CommandException {
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
                if (command.getArguments() != -1 && command.getArguments() != args.length) {
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
                    	base.lang.setCommand("/"+mainCommand+" "+cmd);
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
}
