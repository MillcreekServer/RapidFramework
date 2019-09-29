package io.github.wysohn.rapidframework2.core.manager.lang;

import io.github.wysohn.rapidframework2.core.interfaces.KeyValueStorage;
import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import org.bukkit.ChatColor;
import util.Validation;

import java.text.DecimalFormat;
import java.util.*;

public class ManagerLanguage extends PluginMain.Manager {
    private final Map<Locale, KeyValueStorage> languageSessions = new HashMap<>();
    private final Map<Enum<? extends Lang>, Lang> languages = new HashMap<>();

    private final Queue<Double> doub = new LinkedList<>();
    private final Queue<Integer> integer = new LinkedList<>();
    private final Queue<Long> llong = new LinkedList<>();
    private final Queue<String> string = new LinkedList<>();
    private final Queue<Boolean> bool = new LinkedList<>();

    private Locale defaultLang = Locale.ENGLISH;

    private DecimalFormat decimalFormat = new DecimalFormat("###,###,###.##");

    public ManagerLanguage(int loadPriority) {
        super(loadPriority);
    }

    public Locale getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(Locale defaultLang) {
        this.defaultLang = defaultLang;
    }

    public void addLanguageStorage(Locale locale, KeyValueStorage storage) {
        languageSessions.put(locale, storage);
    }

    public void setDecimalFormat(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
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
     * @param value
     * @return true if there was no same ManagerLanguage already registered
     */
    public boolean registerLanguage(Enum<? extends Lang> lang, Lang value) {
        if (languages.containsKey(lang)) {
            return false;
        } else {
            languages.put(lang, value);
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
                        case "bool":
                            Boolean value = this.bool.poll();
                            if (value == null) {
                                str = leftStr + "null" + rightStr;
                            } else {
                                ChatColor color = value ? ChatColor.GREEN : ChatColor.RED;
                                str = leftStr + color + value + ChatColor.getLastColors(leftStr) + rightStr;
                            }
                            break;
                        default:
                            str = leftStr + "?" + rightStr;
                            break;
                    }
                }
            }

            strings.set(i, str);
        }
    }

    public String[] parse(Locale locale, Enum<? extends Lang> lang, PreParseHandle handle) {
        if (locale == null)
            locale = defaultLang;

        Validation.assertNotNull(lang);
        Validation.assertNotNull(handle);

        if (!languages.containsKey(lang)) {
            main().getLogger().severe("Lang " + lang + " is not registered.");
            return new String[0];
        }

        KeyValueStorage storage = languageSessions.getOrDefault(locale, languageSessions.get(defaultLang));
        if (storage == null) {
            main().getLogger().severe("The language session is not loaded for " + locale);
            return new String[0];
        }

        String configName = convertToConfigName(lang);
        List<String> values = storage.get(configName);
        if (values == null) {
            Lang l = languages.get(lang);
            values = Arrays.asList(l.getEngDefault());

            main().getLogger().fine("Using default value for " + lang);
        }

        handle.onParse(this);
        replaceVariables(values);

        this.doub.clear();
        this.integer.clear();
        this.string.clear();
        this.bool.clear();
        this.llong.clear();

        return values == null ? new String[0] : values.toArray(new String[0]);
    }

    public String[] parse(ICommandSender sender, Enum<? extends Lang> lang, PreParseHandle handle) {
        Validation.assertNotNull(sender);

        return parse(sender.getLocale(), lang, handle);
    }

    public String[] parse(ICommandSender sender, Enum<? extends Lang> lang) {
        Validation.assertNotNull(sender);

        return parse(sender.getLocale(), lang, (managerLanguage -> {
        }));
    }

    public String[] parse(Enum<? extends Lang> lang, PreParseHandle handle) {
        return parse((Locale) null, lang, handle);
    }

    public String[] parse(Enum<? extends Lang> lang) {
        return parse((Locale) null, lang, (managerLanguage -> {
        }));
    }

    public String parseFirst(Locale locale, Enum<? extends Lang> lang, PreParseHandle handle) {
        String[] parsed = parse(locale, lang, handle);
        return parsed.length > 0 ? parsed[0] : "NULL";
    }

    public String parseFirst(ICommandSender sender, Enum<? extends Lang> lang, PreParseHandle handle) {
        Validation.assertNotNull(sender);

        return parseFirst(sender.getLocale(), lang, handle);
    }

    public String parseFirst(ICommandSender sender, Enum<? extends Lang> lang) {
        Validation.assertNotNull(sender);

        return parseFirst(sender.getLocale(), lang, (managerLanguage -> {
        }));
    }

    public String parseFirst(Enum<? extends Lang> lang, PreParseHandle handle) {
        return parseFirst((Locale) null, lang, handle);
    }

    public String parseFirst(Enum<? extends Lang> lang) {
        return parseFirst((Locale) null, lang, (managerLanguage -> {
        }));
    }

    public void sendMessage(ICommandSender commandSender, Enum<? extends Lang> lang, PreParseHandle handle){
        String[] parsed = parse(commandSender, lang, handle);
        commandSender.sendMessage(parsed);
    }

    public void sendMessage(ICommandSender commandSender, Enum<? extends Lang> lang) {
        sendMessage(commandSender, lang, (managerLanguage -> {
        }));
    }

    private static String convertToConfigName(Enum<? extends Lang> lang) {
        return lang.name().replaceAll("_", ".");
    }
}
