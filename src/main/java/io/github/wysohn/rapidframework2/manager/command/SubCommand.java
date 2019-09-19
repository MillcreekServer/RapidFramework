package io.github.wysohn.rapidframework2.manager.command;

import io.github.wysohn.rapidframework2.interfaces.ICommandSender;
import io.github.wysohn.rapidframework2.main.PluginMain;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand<Sender extends ICommandSender> {
    final PluginMain main;
    final String name;
    final int nArguments;

    String[] aliases = new String[0];
    String permission;
    private List<ArgumentMapper> argumentMappers = new ArrayList<>();

    public SubCommand(PluginMain main, String name, int nArguments) {
        this.main = main;
        this.name = name;
        this.nArguments = nArguments;
    }

    public SubCommand(PluginMain main, String name) {
        this(main, name, -1);
    }

    public boolean execute(Sender sender, String commandLabel, String[] args) {
        if (nArguments != -1 && args.length != nArguments)
            return false;

        Arguments argsObj = new Arguments(sender, args);


    }

    public class Arguments implements Iterable<String> {
        private Sender sender;
        private String[] args;

        public Arguments(Sender sender, String[] args) {
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
         * @param def   the value to be used if index is out of range
         * @return the argument; null if argument conversion fails. If null was
         * returned, the error message is already sent to the sender.
         */
        @SuppressWarnings("unchecked")
        public <T> T get(int index, T def) {
            try {
                if (index >= args.length)
                    return def;

                if (index < argumentMappers.size())
                    return (T) argumentMappers.get(index).apply(args[index]);
                else
                    return (T) ArgumentMapper.IDENTITY.apply(args[index]);
            } catch (InvalidArgumentException e) {
                base.lang.addString(args[index]);
                base.sendMessage(sender, e.lang);
            }

            return null;
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
