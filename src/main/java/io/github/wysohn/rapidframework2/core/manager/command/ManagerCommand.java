package io.github.wysohn.rapidframework2.core.manager.command;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.interfaces.entity.IPermissionHolder;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.lang.DefaultLangs;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public void addCommand(SubCommand cmd) {
        commandMap.register(cmd);
    }

    public boolean onCommand(ICommandSender sender, String command, String label, String[] args_in) {
        if(!mainCommand.equals(command))
            return true;

        final String[] args;
        if (args_in.length == 0) {
            args = new String[]{defaultCommand};
        } else {
            args = args_in;
        }

        StringBuilder cmdLine = new StringBuilder();
        for (String str : args) {
            cmdLine.append(str);
            cmdLine.append(' ');
        }

        if (!commandMap.dispatch(main(), sender, cmdLine.toString())) {
            int page = 0;
            if (args.length > 1) {
                main().lang().sendMessage(sender, DefaultLangs.General_NotInteger, (managerLanguage -> {
                    managerLanguage.addString(args[1]);
                }));
                return true;
            }

            this.showHelp(label, sender, page);

            return true;
        }

        return true;
    }

    /**
     * @param sender
     * @param page   0~size()
     */
    public void showHelp(String label, final ICommandSender sender, int page) {
        List<SubCommand> list = commandMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(cmd -> sender.hasPermission(cmd.permission))
                .collect(Collectors.toList());

        main().lang().sendMessage(sender, DefaultLangs.General_Line);

        main().lang().sendMessage(sender, DefaultLangs.General_Header, (managerLanguage -> {
            managerLanguage.addString(main().getPluginName());
        }));
        sender.sendMessage("");

        int max = main().conf().get("command.help.sentenceperpage");

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

            // description
            c.description.handle.onParse(main().lang());
            String descValue = main().lang().parseFirst(sender, c.description.lang);

            main().lang().sendMessage(sender, DefaultLangs.Command_Format_Description, (managerLanguage -> {
                managerLanguage.addString(label);
                managerLanguage.addString(c.name);
                managerLanguage.addString(descValue);
            }));

            StringBuilder builder = new StringBuilder();

            // aliases
            StringBuilder builderAliases = new StringBuilder();
            for (String alias : c.aliases) {
                builderAliases.append(' ');
                builderAliases.append(alias);
            }

            if (builderAliases.length() > 0) {
                builder.append(main().lang().parseFirst(sender, DefaultLangs.Command_Format_Aliases, (managerLanguage -> {
                    managerLanguage.addString(builderAliases.toString());
                })));
                builder.append('\n');
            }

            // usages
            c.usage.stream()
                    .forEach(dynamicLang -> {
                        dynamicLang.handle.onParse(main().lang());
                        String[] usageVals = main().lang().parse(sender, dynamicLang.lang);

                        for (String usage : usageVals) {
                            builder.append(main().lang().parseFirst(sender, DefaultLangs.Command_Format_Usage, (managerLanguage -> {
                                managerLanguage.addString(usage);
                            })));
                            builder.append('\n');
                        }
                    });

            sender.sendMessage(builder.toString());
        }
    }

    public List<String> onTabComplete(ICommandSender sender, String command, String alias, String[] args){
        return null;
    }

}
