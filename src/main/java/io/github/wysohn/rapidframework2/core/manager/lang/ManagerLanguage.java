package io.github.wysohn.rapidframework2.core.manager.lang;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.common.KeyValueStorageAdapter;
import io.github.wysohn.rapidframework2.core.manager.common.message.IMessageSender;
import io.github.wysohn.rapidframework2.core.manager.common.message.Message;
import io.github.wysohn.rapidframework2.core.manager.common.message.MessageBuilder;
import util.JarUtil;
import util.Validation;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ManagerLanguage extends PluginMain.Manager {
    private final Map<Locale, LanguageSession> languageSessions = new HashMap<>();
    private final Set<Lang> languages = new HashSet<>();

    private final Queue<Double> doub = new LinkedList<>();
    private final Queue<Integer> integer = new LinkedList<>();
    private final Queue<Long> llong = new LinkedList<>();
    private final Queue<String> string = new LinkedList<>();
    private final Queue<Boolean> bool = new LinkedList<>();

    private final LanguageSessionFactory sessionFactory;

    private IMessageSender messageSender = new IMessageSender() {
        @Override
        public boolean isJsonEnabled() {
            return false;
        }
    };
    private Locale defaultLang = Locale.ENGLISH;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###,###.##");

    public ManagerLanguage(int loadPriority, LanguageSessionFactory sessionFactory) {
        super(loadPriority);
        this.sessionFactory = sessionFactory;
    }

    public void setMessageSender(IMessageSender messageSender) {
        Validation.assertNotNull(messageSender);

        this.messageSender = messageSender;
    }

    public Locale getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(Locale defaultLang) {
        this.defaultLang = defaultLang;
    }

    public void setDecimalFormat(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
    }

    @Override
    public void enable() throws Exception {
        JarUtil.copyFromJar("lang/**", main().getPluginDirectory(), JarUtil.CopyOption.COPY_IF_NOT_EXIST);
    }

    @Override
    public void load() throws Exception {
        languageSessions.clear();

        for(Locale locale : sessionFactory.getLocales(main())){
            LanguageSession session = sessionFactory.create(locale);
            if(session != null){
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

    /**
     * @param lang
     * @return true if there was no same ManagerLanguage already registered
     */
    public boolean registerLanguage(Lang lang) {
        return languages.add(lang);
    }

    public boolean isJsonEnabled() {
        return this.messageSender.isJsonEnabled();
    }

    private void replaceVariables(List<String> strings) {
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
                            str = leftStr + "null" + rightStr;
                            break;
                    }
                }
            }

            strings.set(i, str);
        }
    }

    public String[] parse(Locale locale, ICommandSender sender, Lang lang, PreParseHandle handle) {
        if (locale == null)
            locale = defaultLang;

        Validation.assertNotNull(lang);
        Validation.assertNotNull(handle);

        if (!languages.contains(lang)) {
            main().getLogger().severe("Lang " + lang + " is not registered.");
            return new String[0];
        }

        LanguageSession session = languageSessions.getOrDefault(locale, languageSessions.get(defaultLang));
        if (session == null) {
            main().getLogger().severe("No language session is loaded. Using temporary session.");
            session = new LanguageSession(new KeyValueStorageAdapter());
            languageSessions.put(defaultLang, session);
        }

        List<String> values = session.translate(lang);
        if (values == null || values.isEmpty()) {
            values = Stream.of(lang.getEngDefault()).collect(Collectors.toList());

            main().getLogger().fine("Using default value for " + lang);
        }

        handle.onParse(sender, this);
        replaceVariables(values);

        this.doub.clear();
        this.integer.clear();
        this.string.clear();
        this.bool.clear();
        this.llong.clear();

        return values.toArray(new String[0]);
    }

    public String[] parse(ICommandSender sender, Lang lang, PreParseHandle handle) {
        Validation.assertNotNull(sender);

        return parse(sender.getLocale(), sender, lang, handle);
    }

    public String[] parse(ICommandSender sender, Lang lang) {
        Validation.assertNotNull(sender);

        return parse(sender.getLocale(), sender, lang, ((sen, langman) -> {
        }));
    }

    public String[] parse(Lang lang, PreParseHandle handle) {
        return parse(null, null, lang, handle);
    }

    public String[] parse(Lang lang) {
        return parse(null, null, lang, ((sen, langman) -> {
        }));
    }

    public String parseFirst(Locale locale, Lang lang, PreParseHandle handle) {
        String[] parsed = parse(locale, null, lang, handle);
        return parsed.length > 0 ? parsed[0] : "NULL";
    }

    public String parseFirst(ICommandSender sender, Lang lang, PreParseHandle handle) {
        Validation.assertNotNull(sender);
        String[] parsed = parse(sender.getLocale(), sender, lang, handle);
        return parsed.length > 0 ? parsed[0] : "NULL";
    }

    public String parseFirst(ICommandSender sender, Lang lang) {
        Validation.assertNotNull(sender);

        return parseFirst(sender.getLocale(), lang, ((sen, langman) -> {
        }));
    }

    public String parseFirst(Lang lang, PreParseHandle handle) {
        return parseFirst((Locale) null, lang, handle);
    }

    public String parseFirst(Lang lang) {
        return parseFirst((Locale) null, lang, ((sen, langman) -> {
        }));
    }

    public void sendMessage(ICommandSender commandSender, Lang lang, PreParseHandle handle) {
        sendMessage(commandSender, lang, handle, false);
    }

    public void sendMessage(ICommandSender commandSender, Lang lang, PreParseHandle handle, boolean conversation) {
        String[] parsed = parse(commandSender, lang, handle);

        Arrays.stream(parsed)
                .map(msg -> MessageBuilder.forMessage(msg).build())
                .forEach(message -> messageSender.send(commandSender, message, conversation));
    }

    public void sendMessage(ICommandSender commandSender, Lang lang) {
        sendMessage(commandSender, lang, false);
    }

    public void sendMessage(ICommandSender commandSender, Lang lang, boolean conversation) {
        sendMessage(commandSender, lang, ((sen, langman) -> {
        }), conversation);
    }

    public void broadcast(Lang lang, PreParseHandle handle) {
        main().getBridge().forEachSender(player -> sendMessage(player, lang, handle));
    }

    public void broadcast(Lang lang) {
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
