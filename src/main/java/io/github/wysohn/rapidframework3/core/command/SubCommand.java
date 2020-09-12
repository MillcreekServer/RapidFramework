package io.github.wysohn.rapidframework3.core.command;

import io.github.wysohn.rapidframework3.core.exceptions.InvalidArgumentException;
import io.github.wysohn.rapidframework3.core.interfaces.ICommandSender;
import io.github.wysohn.rapidframework3.core.interfaces.command.CommandAction;
import io.github.wysohn.rapidframework3.core.interfaces.command.IArgumentMapper;
import io.github.wysohn.rapidframework3.core.interfaces.command.ITabCompleter;
import io.github.wysohn.rapidframework3.core.interfaces.language.ILang;
import io.github.wysohn.rapidframework3.core.interfaces.language.ILangParser;
import io.github.wysohn.rapidframework3.core.language.DynamicLang;
import io.github.wysohn.rapidframework3.core.main.PluginMain;

import java.util.*;
import java.util.function.Predicate;

public class SubCommand {
    final PluginMain main;
    final String name;
    final int nArguments;

    String[] aliases = new String[0];
    String permission;
    List<Predicate<ICommandSender>> predicates = new ArrayList<>();
    DynamicLang description;
    List<DynamicLang> usage = new ArrayList<>();
    Map<String, DynamicLang> specifications = new HashMap<>();
    boolean doubleCheck = false;
    private CommandAction action;
    private List<IArgumentMapper> argumentMappers = new ArrayList<>();
    private List<ITabCompleter> tabCompleters = new ArrayList<>();

    SubCommand(PluginMain main, String name, int nArguments) {
        this.main = main;
        this.name = name;
        this.nArguments = nArguments;
    }

    SubCommand(PluginMain main, String name) {
        this(main, name, -1);
    }

    public boolean execute(ICommandSender sender, String commandLabel, String[] args) {
        if (nArguments != -1 && args.length != nArguments)
            return false;

        Arguments argsObj = new Arguments(sender, args);

        if (action != null)
            return action.execute(sender, argsObj);
        else
            return true;
    }

    public List<String> tabHint(int index) {
        if (index < tabCompleters.size())
            return tabCompleters.get(index).getHint();
        else
            return TabCompleters.EMPTY.getHint();
    }

    /**
     * Get list of possible tab completes.
     *
     * @param index the index of unfinished argument
     * @param part  the partially finished argument.
     * @return list of possible completions
     */
    public List<String> tabComplete(int index, String part) {
        if (index < tabCompleters.size())
            return tabCompleters.get(index).getCandidates(part);
        else
            return TabCompleters.EMPTY.getCandidates(part);
    }

    public static class Builder {
        private SubCommand command;

        public Builder(PluginMain main, String cmd, int numArgs) {
            command = new SubCommand(main, cmd, numArgs);
            command.permission = main.getRootPermission() + "." + cmd;
        }

        public Builder(PluginMain main, String cmd) {
            this(main, cmd, 0);
        }

        public Builder withAlias(String... alias) {
            command.aliases = alias;
            return this;
        }

        public Builder withPermission(String permission) {
            command.permission = permission;
            return this;
        }

        public Builder addPredicate(Predicate<ICommandSender> predicate) {
            command.predicates.add(predicate);
            return this;
        }

        public Builder withDescription(ILang description) {
            return withDescription(description, (sender, managerLanguage) -> {
            });
        }

        public Builder withDescription(ILang description, ILangParser handle) {
            command.description = new DynamicLang(description, handle);
            return this;
        }

        /**
         * ${command} is built-in placeholder for 'this command' without slash(/)
         *
         * @param usage
         * @return
         */
        public Builder addUsage(ILang usage) {
            return addUsage(usage, ((sender, managerLanguage) -> {
            }));
        }

        /**
         * ${command} is built-in placeholder for 'this command' without slash(/)
         *
         * @param lang
         * @return
         */
        public Builder addUsage(ILang lang, ILangParser handle) {
            command.usage.add(new DynamicLang(lang, handle));
            return this;
        }

        /**
         * Add specification to show under this subcommand.
         * <p>
         * Ex) if the plugin main command is xyz, and this subcommand is wow, then
         * /xyz wow [subpart...] [lang]
         *
         * @param subpart
         * @param lang
         * @return
         */
        public Builder addSpecification(String subpart, ILang lang) {
            return addSpecification(subpart, lang, ((sender, managerLanguage) -> {
            }));
        }

        /**
         * Add specification to show under this subcommand.
         * <p>
         * Ex) if the plugin main command is xyz, and this subcommand is wow, then
         * /xyz wow [subpart...] [lang]
         *
         * @param subpart
         * @param lang
         * @param handle
         * @return
         */
        public Builder addSpecification(String subpart, ILang lang, ILangParser handle) {
            command.specifications.put(subpart, new DynamicLang(lang, handle));
            return this;
        }

        public Builder action(CommandAction action) {
            command.action = action;
            return this;
        }

        public Builder addArgumentMapper(int index, IArgumentMapper mapper) {
            if (mapper == null)
                throw new RuntimeException(
                        "Cannot use null for mapper! Use ArgumentMapper.IDENTITY if mapping is not required.");

            while (command.argumentMappers.size() <= index)
                command.argumentMappers.add(ArgumentMappers.IDENTITY);
            command.argumentMappers.set(index, mapper);

            return this;
        }

        public Builder addTabCompleter(int index, ITabCompleter completer) {
            if (completer == null)
                throw new RuntimeException(
                        "Cannot use null for completer! Use TabCompleter.EMPTY if mapping is not required.");

            while (command.tabCompleters.size() <= index)
                command.tabCompleters.add(TabCompleters.EMPTY);
            command.tabCompleters.set(index, completer);

            return this;
        }

        public Builder needDoubleCheck() {
            command.doubleCheck = true;
            return this;
        }

        public SubCommand create() {
            return command;
        }
    }

    public class Arguments implements Iterable<String> {
        private ICommandSender sender;
        private String[] args;

        public Arguments(ICommandSender sender, String[] args) {
            super();
            this.sender = sender;
            this.args = args;
        }

        public int size() {
            return args.length;
        }

        /**
         * Try to get argument at the index. If index is out of range, the provided
         * default value will be used. You may add ArgumentMapper when building the
         * command to automatically convert the input to appropriate value (e.g.
         * argument to integer). If the ArgumentMapper cannot convert the argument for
         * some reason (like trying to convert non-number string to an integer), it will
         * automatically show error message to the user.
         *
         * @param index index of argument
         * @return the argument; Empty Optional if conversion fails or argument index out of bound.
         * If Empty Optional was returned, the error message is already sent to the sender.
         */
        @SuppressWarnings("unchecked")
        public <T> Optional<T> get(int index) {
            try {
                if (index >= args.length)
                    return Optional.empty();

                if (index < argumentMappers.size())
                    return Optional.of((T) argumentMappers.get(index).apply(args[index]));
                else
                    return Optional.of((T) ArgumentMappers.IDENTITY.apply(args[index]));
            } catch (InvalidArgumentException e) {
                e.getDynamicLang().parser.onParse(sender, main.lang());
                main.lang().sendMessage(sender, e.getDynamicLang().lang);
            }

            return Optional.empty();
        }

        /**
         * get all arguments starting from 'index' to the end as a string
         *
         * @param index
         * @return null if index is out of bound; string otherwise
         */
        public String getAsString(int index) {
            return getAsString(index, args.length - 1);
        }

        /**
         * get all arguments starting from 'index' to the 'end'
         *
         * @param index inclusive
         * @param end   inclusive
         * @return null if index is out of bound; string otherwise
         */
        public String getAsString(int index, int end) {
            if (index > args.length - 1)
                return null;

            StringBuilder builder = new StringBuilder(args[index]);
            for (int i = index + 1; i <= end; i++) {
                builder.append(" " + args[i]);
            }
            return builder.toString();

        }

        @Override
        public Iterator<String> iterator() {
            return new ArgumentIterator();
        }

        private class ArgumentIterator implements Iterator<String> {
            private int index = -1;

            @Override
            public boolean hasNext() {
                return index + 1 < args.length;
            }

            @Override
            public String next() {
                return args[++index];
            }
        }
    }
}
