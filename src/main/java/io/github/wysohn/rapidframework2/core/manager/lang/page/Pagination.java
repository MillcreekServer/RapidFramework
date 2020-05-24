package io.github.wysohn.rapidframework2.core.manager.lang.page;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.common.message.Message;
import io.github.wysohn.rapidframework2.core.manager.common.message.MessageBuilder;
import io.github.wysohn.rapidframework2.core.manager.lang.DefaultLangs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Pagination<T> {
    private final ExecutorService exec = Executors.newCachedThreadPool();

    public static String LEFT_ARROW = "&8[&a<---&8]";
    public static String HOME = "&8[&aHome&8]";
    public static String RIGHT_ARROW = "&8[&a--->&8]";

    private final PluginMain main;
    private final DataProvider<T> dataProvider;
    private final int max;
    private final String title;

    private final String cmd;

    public Pagination(PluginMain main, DataProvider<T> dataProvider, int max, String title, String cmd) {
        this.main = main;
        this.dataProvider = dataProvider;
        this.max = max;
        this.title = title;
        this.cmd = cmd;
    }

    /**
     * @param sender
     * @param page      0 ~ size (exclusive, yet out of bound value is acceptable)
     * @param messageFn
     */
    public void show(ICommandSender sender, int page, MessageConverter<T> messageFn) {
        main.lang().sendMessage(sender, DefaultLangs.General_Line);
        main.lang().sendMessage(sender, DefaultLangs.General_Header, ((sen, langman) ->
                langman.addString(title)));
        main.lang().sendRawMessage(sender, MessageBuilder.forMessage("").build());

        exec.submit(() -> dataProvider.sync(() -> {
            int remainder = dataProvider.size() % max;
            int divided = dataProvider.size() / max;
            int outof = remainder == 0 ? divided : divided + 1;

            int p = Math.max(page, 0);
            p = Math.min(p, outof - 1);

            int index;
            for (index = p * max; index >= 0 && index < (p + 1) * max; index++) {
                if (index >= dataProvider.size())
                    break;

                T val = dataProvider.get(index);
                if (dataProvider.omit(val)) {
                    continue;
                }

                main.lang().sendRawMessage(sender, messageFn.convert(sender, val, index));
            }

            main.lang().sendMessage(sender, DefaultLangs.General_Line);

            if (main.lang().isJsonEnabled()) {
                final String cmdPrev = this.cmd + " " + p;
                final String cmdHome = this.cmd;
                final String cmdNext = this.cmd + " " + (p + 2);


                Message[] btns = MessageBuilder.forMessage("")
                        .append(LEFT_ARROW)
                        .withHoverShowText(cmdPrev)
                        .withClickRunCommand(cmdPrev)
                        .append(HOME)
                        .withHoverShowText(cmdHome)
                        .withClickRunCommand(cmdHome)
                        .append(RIGHT_ARROW)
                        .withHoverShowText(cmdNext)
                        .withClickRunCommand(cmdNext)
                        .build();

                main.lang().sendRawMessage(sender, btns);
            } else {
                main.lang().sendMessage(sender, DefaultLangs.Command_Help_TypeHelpToSeeMore, ((sen, langman) ->
                        langman.addString(this.cmd + " <page>")));
            }

            final int pageCopy = p + 1;
            main.lang().sendMessage(sender, DefaultLangs.Command_Help_PageDescription, ((sen, langman) ->
                    langman.addInteger(pageCopy).addInteger(outof)));
            sender.sendMessageRaw("");
        }));
    }

    public interface DataProvider<T> {
        int size();

        T get(int index);

        default void sync(Runnable run) {
            run.run();
        }

        default boolean omit(T val) {
            return false;
        }
    }

    @FunctionalInterface
    public interface MessageConverter<T> {
        Message[] convert(ICommandSender sender, T from, int index);
    }
}
