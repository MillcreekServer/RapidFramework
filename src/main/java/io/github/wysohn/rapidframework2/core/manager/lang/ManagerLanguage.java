package io.github.wysohn.rapidframework2.core.manager.lang;

import io.github.wysohn.rapidframework.utils.files.JarUtil;
import io.github.wysohn.rapidframework2.core.interfaces.KeyValueStorage;
import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.common.KeyValueStorageAdapter;
import util.Validation;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ManagerLanguage extends PluginMain.Manager {
    private final Map<Locale, LanguageSession> languageSessions = new HashMap<>();
    private final Map<Class<? extends Lang>, Lang> languages = new HashMap<>();

    private final Queue<Double> doub = new LinkedList<>();
    private final Queue<Integer> integer = new LinkedList<>();
    private final Queue<Long> llong = new LinkedList<>();
    private final Queue<String> string = new LinkedList<>();
    private final Queue<Boolean> bool = new LinkedList<>();

    private final LanguageSessionFactory sessionFactory;

    private Locale defaultLang = Locale.ENGLISH;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###,###.##");

    public ManagerLanguage(int loadPriority, LanguageSessionFactory sessionFactory) {
        super(loadPriority);
        this.sessionFactory = sessionFactory;
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
        JarUtil.copyFolderFromJar("lang", main().getPluginDirectory(), JarUtil.CopyOption.COPY_IF_NOT_EXIST);
    }

    @Override
    public void load() throws Exception {
        languageSessions.clear();

        for(Locale locale : sessionFactory.getLocales(main())){
            LanguageSession session = sessionFactory.create(locale);
            if(session != null){
                languageSessions.put(locale, session);
            }
        }
    }

    @Override
    public void disable() throws Exception {

    }

    public void addDouble(double doub) {
        this.doub.add(Double.valueOf(doub));
    }

    public void addInteger(int integer) {
        this.integer.add(Integer.valueOf(integer));
    }

    public void addString(String string) {
        Validation.assertNotNull(string);

        this.string.add(string);
    }

    public void addString(String[] strs) {
        for (String str : strs)
            addString(str);
    }

    public void addBoolean(boolean bool) {
        this.bool.add(Boolean.valueOf(bool));
    }

    public void addLong(long llong) {
        this.llong.add(llong);
    }

    /**
     * @param lang
     * @return true if there was no same ManagerLanguage already registered
     */
    public boolean registerLanguage(Lang lang) {
        if (languages.containsKey(lang.getClass())) {
            return false;
        } else {
            languages.put(lang.getClass(), lang);
            return true;
        }
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

    public String[] parse(Locale locale, Lang lang, PreParseHandle handle) {
        if (locale == null)
            locale = defaultLang;

        Validation.assertNotNull(lang);
        Validation.assertNotNull(handle);

        if (!languages.containsKey(lang.getClass())) {
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
        if (values == null) {
            values = Stream.of(lang.getEngDefault()).collect(Collectors.toList());

            main().getLogger().fine("Using default value for " + lang);
        }

        handle.onParse(this);
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

        return parse(sender.getLocale(), lang, handle);
    }

    public String[] parse(ICommandSender sender, Lang lang) {
        Validation.assertNotNull(sender);

        return parse(sender.getLocale(), lang, (managerLanguage -> {
        }));
    }

    public String[] parse(Lang lang, PreParseHandle handle) {
        return parse((Locale) null, lang, handle);
    }

    public String[] parse(Lang lang) {
        return parse((Locale) null, lang, (managerLanguage -> {
        }));
    }

    public String parseFirst(Locale locale, Lang lang, PreParseHandle handle) {
        String[] parsed = parse(locale, lang, handle);
        return parsed.length > 0 ? parsed[0] : "NULL";
    }

    public String parseFirst(ICommandSender sender, Lang lang, PreParseHandle handle) {
        Validation.assertNotNull(sender);

        return parseFirst(sender.getLocale(), lang, handle);
    }

    public String parseFirst(ICommandSender sender, Lang lang) {
        Validation.assertNotNull(sender);

        return parseFirst(sender.getLocale(), lang, (managerLanguage -> {
        }));
    }

    public String parseFirst(Lang lang, PreParseHandle handle) {
        return parseFirst((Locale) null, lang, handle);
    }

    public String parseFirst(Lang lang) {
        return parseFirst((Locale) null, lang, (managerLanguage -> {
        }));
    }

    public void sendMessage(ICommandSender commandSender, Lang lang, PreParseHandle handle){
        String[] parsed = parse(commandSender, lang, handle);
        commandSender.sendMessage(parsed);
    }

    public void sendMessage(ICommandSender commandSender, Lang lang) {
        sendMessage(commandSender, lang, (managerLanguage -> {
        }));
    }

    private static String convertToConfigName(Lang lang) {
        return lang.name().replaceAll("_", ".");
    }

    private static String getLastColors(String str){
        for(int i = str.length() - 1; i >= 0 ; i--){
            if(str.charAt(i) == '&' && i < str.length() - 2){
                char c = str.charAt(i+1);

                if(('a' <= c && c <= 'f') || Character.isDigit(c))
                    return "&"+str.charAt(i+1);
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
