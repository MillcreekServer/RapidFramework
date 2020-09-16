package io.github.wysohn.rapidframework3.core.language;

import io.github.wysohn.rapidframework3.core.message.Message;
import io.github.wysohn.rapidframework3.core.message.MessageBuilder;
import io.github.wysohn.rapidframework3.interfaces.ICommandSender;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Pagination<T> {
    private final ExecutorService exec = Executors.newCachedThreadPool();

    public static String LEFT_ARROW = "&8[&a<---&8]";
    public static String HOME = "&8[&aHome&8]";
    public static String RIGHT_ARROW = "&8[&a--->&8]";

    private final ManagerLanguage lang;
    private final DataProvider<T> dataProvider;
    private final int max;
    private final String title;

    private final String cmd;

    public Pagination(ManagerLanguage lang, DataProvider<T> dataProvider, int max, String title, String cmd) {
        this.lang = lang;
        this.dataProvider = dataProvider;
        this.max = max;
        this.title = title;
        this.cmd = cmd;
    }

    public static <T> Pagination<T> list(ManagerLanguage lang, List<T> list, int max, String title, String cmd) {
        DataProvider<T> provider = new ListWrapper<>(list);
        return new Pagination<>(lang, provider, max, title, cmd);
    }

    /**
     * @param sender
     * @param page      0 ~ size (exclusive, yet out of bound value is acceptable)
     * @param messageFn
     */
    public void show(ICommandSender sender, int page, MessageConverter<T> messageFn) {
        lang.sendMessage(sender, DefaultLangs.General_Line);
        lang.sendMessage(sender, DefaultLangs.General_Header, ((sen, langman) ->
                langman.addString(title)));
        lang.sendRawMessage(sender, MessageBuilder.forMessage("").build());

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

                lang.sendRawMessage(sender, messageFn.convert(sender, val, index));
            }

            lang.sendMessage(sender, DefaultLangs.General_Line);

            if (lang.isJsonEnabled()) {
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

                lang.sendRawMessage(sender, btns);
            } else {
                lang.sendMessage(sender, DefaultLangs.Command_Help_TypeHelpToSeeMore, ((sen, langman) ->
                        langman.addString(this.cmd + " <page>")));
            }

            final int pageCopy = p + 1;
            lang.sendMessage(sender, DefaultLangs.Command_Help_PageDescription, ((sen, langman) ->
                    langman.addInteger(pageCopy).addInteger(outof)));
            sender.sendMessageRaw("");
        }));
    }

    public void shutdown() throws InterruptedException {
        exec.shutdownNow().forEach(Runnable::run);
        exec.awaitTermination(10, TimeUnit.SECONDS);
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
