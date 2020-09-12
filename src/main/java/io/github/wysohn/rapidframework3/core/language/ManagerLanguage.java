package io.github.wysohn.rapidframework3.core.language;

import io.github.wysohn.rapidframework2.tools.JarUtil;
import io.github.wysohn.rapidframework2.tools.Validation;
import io.github.wysohn.rapidframework3.core.interfaces.ICommandSender;
import io.github.wysohn.rapidframework3.core.interfaces.language.ILang;
import io.github.wysohn.rapidframework3.core.interfaces.language.ILangParser;
import io.github.wysohn.rapidframework3.core.interfaces.language.ILangSession;
import io.github.wysohn.rapidframework3.core.interfaces.language.ILangSessionFactory;
import io.github.wysohn.rapidframework3.core.interfaces.message.IBroadcaster;
import io.github.wysohn.rapidframework3.core.interfaces.message.IMessageSender;
import io.github.wysohn.rapidframework3.core.main.Manager;
import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.core.message.Message;
import io.github.wysohn.rapidframework3.core.message.MessageBuilder;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h1>ManagerLanguage</h1>
 * <p>
 * The class which automatically translation {@link ILang} into appropriate locale.
 * The locale respects the {@link Locale} returned by {@link ICommandSender#getLocale()}.
 * If the translation file is not specified for the returned {@link Locale}, {@link Locale#ENGLISH}
 * will be used as default.
 * <hr>
 * First, client instantiate the class with the {@link ILangSessionFactory} which will generate
 * appropriate {@link ILangSession} as this class' need. Each {@link ILangSession} return
 * String which is equivalent to the {@link ILang} passed to it, and this is how translation work for
 * different locales.
 * <hr>
 * Then, the given String may contain various 'placeholder' which the client can dynamically fill as needed.
 * For example. the returned String from {@link ILangSession} may contain special String, which starts with
 * ${, followed by keyword, and then } (ex. ${integer}). These placeholder will be replaced one by one by insertion
 * order of 'add' methods of this class. So if the returned String has ${integer} ${integer} ${integer}, these
 * placeholders will be replaced with the values inserted by {@link ManagerLanguage#addInteger(int)} by
 * First In First Out (FIFO) manner.
 * <hr>
 * <h2>Placeholders</h2>
 * <ul>
 *     <li>${integer}</li>
 *     <li>${double}</li>
 *     <li>${long}</li>
 *     <li>${string}</li>
 *     <li>${boolean}</li>
 *     <li>${date}</li>
 * </ul>
 * ${date} may can add formats: ${date style} ${date style timezone}
 * <br><br>
 * style:
 * <ul>
 *     <li>default</li>
 *     <li>short</li>
 *     <li>medium</li>
 *     <li>long</li>
 *     <li>full</li>
 * </ul>
 * timezone:
 * <ul><li>refer {@link TimeZone#getTimeZone(String)}</li></ul>
 * example) ${date full GMT+09:00}
 * <br>
 *
 * @author wysohn
 */
public class ManagerLanguage extends Manager {
    private final Set<ILang> languages;
    private final ILangSessionFactory langSessionFactory;
    private final IMessageSender messageSender;
    private final IBroadcaster broadcaster;
    private final DecimalFormat decimalFormat;
    private final Locale defaultLang;

    private final Map<Locale, ILangSession> languageSessions = new HashMap<>();

    private final Queue<Double> doub = new LinkedList<>();
    private final Queue<Integer> integer = new LinkedList<>();
    private final Queue<Long> llong = new LinkedList<>();
    private final Queue<String> string = new LinkedList<>();
    private final Queue<Boolean> bool = new LinkedList<>();
    private final Queue<Date> date = new LinkedList<>();
    private final Map<String, Integer> dateStyleMap = new HashMap<>();

    {
        dateStyleMap.put("default", DateFormat.DEFAULT);
        dateStyleMap.put("short", DateFormat.SHORT);
        dateStyleMap.put("medium", DateFormat.MEDIUM);
        dateStyleMap.put("long", DateFormat.LONG);
        dateStyleMap.put("full", DateFormat.FULL);
    }

    @Inject
    public ManagerLanguage(PluginMain main,
                           Set<ILang> languages,
                           ILangSessionFactory langSessionFactory,
                           IMessageSender messageSender,
                           IBroadcaster broadcaster,
                           DecimalFormat decimalFormat,
                           Locale defaultLang) {
        super(main);
        this.languages = languages;
        this.langSessionFactory = langSessionFactory;
        this.messageSender = messageSender;
        this.broadcaster = broadcaster;
        this.decimalFormat = decimalFormat;
        this.defaultLang = defaultLang;
    }

    public Locale getDefaultLang() {
        return defaultLang;
    }

    @Override
    public void enable() throws Exception {

    }

    @Override
    public void load() throws Exception {
        JarUtil.copyFromJar("lang/*", main().getPluginDirectory(), JarUtil.CopyOption.COPY_IF_NOT_EXIST);

        languageSessions.clear();

        for (Locale locale : langSessionFactory.getLocales()) {
            ILangSession session = langSessionFactory.create(locale);
            if (session != null) {
                languageSessions.put(locale, session);
                session.fill(languages);
            }
        }
    }

    @Override
    public void disable() throws Exception {

    }

    public ManagerLanguage addDouble(double doub) {
        this.doub.add(Double.valueOf(doub));
        return this;
    }

    public ManagerLanguage addInteger(int integer) {
        this.integer.add(Integer.valueOf(integer));
        return this;
    }

    public ManagerLanguage addString(String string) {
        Validation.assertNotNull(string);

        this.string.add(string);
        return this;
    }

    public ManagerLanguage addString(String[] strs) {
        for (String str : strs)
            addString(str);

        return this;
    }

    public ManagerLanguage addBoolean(boolean bool) {
        this.bool.add(bool);
        return this;
    }

    public ManagerLanguage addLong(long llong) {
        this.llong.add(llong);
        return this;
    }

    public ManagerLanguage addDate(Date date) {
        this.date.add(date);
        return this;
    }

    public boolean isJsonEnabled() {
        return this.messageSender.isJsonEnabled();
    }

    private void replaceVariables(Locale locale, List<String> strings) {
        for (int i = 0; i < strings.size(); i++) {
            String str = strings.get(i);
            if (str == null)
                continue;

            if (str.contains("${")) {
                int start = -1;
                int end = -1;

                while (!((start = str.indexOf("${")) == -1 || (end = str.indexOf("}")) == -1)) {

                    String leftStr = str.substring(0, start);
                    String rightStr = str.substring(end + 1);

                    String varName = str.substring(start + 2, end);

                    switch (varName) {
                        case "double":
                            Double val = this.doub.poll();
                            String msg = null;
                            if (val != null) {
                                if (Double.isFinite(val))
                                    msg = decimalFormat.format(val);
                                else
                                    msg = "NaN";
                            }
                            str = leftStr + msg + rightStr;
                            break;
                        case "integer":
                            str = leftStr + this.integer.poll() + rightStr;
                            break;
                        case "long":
                            str = leftStr + this.llong.poll() + rightStr;
                            break;
                        case "string":
                            str = leftStr + this.string.poll() + rightStr;
                            break;
                        case "boolean":
                            Boolean value = this.bool.poll();
                            if (value == null) {
                                str = leftStr + "null" + rightStr;
                            } else {
                                String color = value ? "&a" : "&c";
                                str = leftStr + color + value + getLastColors(leftStr) + rightStr;
                            }
                            break;
                        default:
                            // ${date} or ${date style} or ${date style UTC}
                            // style: default, short, medium, long, full
                            if (varName.startsWith("date")) {
                                varName = varName.trim();
                                String[] split = varName.split(" ");

                                Date d = date.poll();
                                DateFormat format = null;
                                TimeZone timeZone = null;
                                if (split.length == 1) {
                                    format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                                            DateFormat.SHORT,
                                            locale);
                                    timeZone = TimeZone.getTimeZone("UTC");
                                } else if (split.length == 2) {
                                    int style = dateStyleMap.getOrDefault(split[1], -1);
                                    if (style > -1)
                                        format = DateFormat.getDateTimeInstance(style, style, locale);
                                    timeZone = TimeZone.getTimeZone("UTC");
                                } else if (split.length == 3) {
                                    int style = dateStyleMap.getOrDefault(split[1], -1);
                                    if (style > -1)
                                        format = DateFormat.getDateTimeInstance(style, style, locale);
                                    timeZone = TimeZone.getTimeZone(split[2]);
                                }

                                if (format == null || timeZone == null) {
                                    str = leftStr + "?illegal 'date' format?" + rightStr;
                                    break;
                                }

                                format.setTimeZone(timeZone);
                                str = leftStr + format.format(d) + rightStr;
                            } else {
                                str = leftStr + "null" + rightStr;
                            }
                            break;
                    }
                }
            }

            strings.set(i, str);
        }
    }

    public String[] parse(Locale locale, ICommandSender sender, ILang lang, ILangParser handle) {
        if (locale == null)
            locale = defaultLang;

        Validation.assertNotNull(lang);
        Validation.assertNotNull(handle);

        if (!languages.contains(lang)) {
            main().getLogger().severe("Lang " + lang + " is not registered.");
            return new String[0];
        }

        ILangSession session = languageSessions.getOrDefault(locale, languageSessions.get(defaultLang));
        if (session == null) {
            main().getLogger().severe("No language session is loaded. Using temporary session.");
            session = new ILangSession() {
                @Override
                public List<String> translate(ILang lang) {
                    return Arrays.asList(lang.getEngDefault().clone());
                }

                @Override
                public void fill(Collection<ILang> values) {

                }
            };
            languageSessions.put(defaultLang, session);
        }

        List<String> values = session.translate(lang);
        if (values == null || values.isEmpty()) {
            values = Stream.of(lang.getEngDefault()).collect(Collectors.toList());

            main().getLogger().fine("Using default value for " + lang);
        }

        handle.onParse(sender, this);
        replaceVariables(locale, values);

        this.doub.clear();
        this.integer.clear();
        this.string.clear();
        this.bool.clear();
        this.llong.clear();

        return values.toArray(new String[0]);
    }

    public String[] parse(ICommandSender sender, ILang lang, ILangParser handle) {
        Validation.assertNotNull(sender);

        return parse(sender.getLocale(), sender, lang, handle);
    }

    public String[] parse(ICommandSender sender, ILang lang) {
        Validation.assertNotNull(sender);

        return parse(sender.getLocale(), sender, lang, ((sen, langman) -> {
        }));
    }

    public String[] parse(ILang lang, ILangParser handle) {
        return parse(null, null, lang, handle);
    }

    public String[] parse(ILang lang) {
        return parse(null, null, lang, ((sen, langman) -> {
        }));
    }

    public String parseFirst(Locale locale, ILang lang, ILangParser handle) {
        String[] parsed = parse(locale, null, lang, handle);
        return parsed.length > 0 ? parsed[0] : "NULL";
    }

    public String parseFirst(ICommandSender sender, ILang lang, ILangParser handle) {
        Validation.assertNotNull(sender);
        String[] parsed = parse(sender.getLocale(), sender, lang, handle);
        return parsed.length > 0 ? parsed[0] : "NULL";
    }

    public String parseFirst(ICommandSender sender, ILang lang) {
        Validation.assertNotNull(sender);

        return parseFirst(sender.getLocale(), lang, ((sen, langman) -> {
        }));
    }

    public String parseFirst(ILang lang, ILangParser handle) {
        return parseFirst((Locale) null, lang, handle);
    }

    public String parseFirst(ILang lang) {
        return parseFirst((Locale) null, lang, ((sen, langman) -> {
        }));
    }

    public void sendMessage(ICommandSender commandSender, ILang lang, ILangParser handle) {
        sendMessage(commandSender, lang, handle, false);
    }

    public void sendMessage(ICommandSender commandSender, ILang lang, ILangParser handle, boolean conversation) {
        String[] parsed = parse(commandSender, lang, handle);

        Arrays.stream(parsed)
                .map(msg -> MessageBuilder.forMessage(msg).build())
                .forEach(message -> messageSender.send(commandSender, message, conversation));
    }

    public void sendMessage(ICommandSender commandSender, ILang lang) {
        sendMessage(commandSender, lang, false);
    }

    public void sendMessage(ICommandSender commandSender, ILang lang, boolean conversation) {
        sendMessage(commandSender, lang, ((sen, langman) -> {
        }), conversation);
    }

    public void broadcast(ILang lang, ILangParser handle) {
        broadcaster.forEachSender(player -> sendMessage(player, lang, handle));
    }

    public void broadcast(ILang lang) {
        broadcast(lang, ((sen, langman) -> {
        }));
    }

    public void sendRawMessage(ICommandSender sender, Message[] message) {
        sendRawMessage(sender, message, false);
    }

    public void sendRawMessage(ICommandSender sender, Message[] message, boolean conversation) {
        messageSender.send(sender, message, conversation);
    }

    private static String getLastColors(String str) {
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) == '&' && i < str.length() - 2) {
                char c = str.charAt(i + 1);

                if (('a' <= c && c <= 'f') || Character.isDigit(c))
                    return "&" + str.charAt(i + 1);
            }
        }

        return "&f";
    }

//    public static void main(String[] ar){
//        System.out.println(getLastColors("&cTest Message &ais this &ho&&"));
//        System.out.println(getLastColors("&cTest Message &ais this &do&&"));
//        System.out.println(getLastColors("&cTest Me&*ssage &iis this &ho&&"));
//    }
}
