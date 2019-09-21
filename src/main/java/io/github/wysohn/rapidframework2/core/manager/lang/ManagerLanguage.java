package io.github.wysohn.rapidframework2.core.manager.lang;

import io.github.wysohn.rapidframework2.core.interfaces.KeyValueStorage;
import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.util.*;

public class ManagerLanguage extends PluginMain.Manager {
    private final Map<Locale, KeyValueStorage> languageSessions = new HashMap<>();
    private final Set<Enum<? extends Lang>> languages = new HashSet<>();

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

    /**
     * @param lang
     * @return true if there was no same ManagerLanguage already registered
     */
    public boolean registerLanguage(Enum<? extends Lang> lang) {
        return languages.add(lang);
    }

    private void replaceVariables(Object sender, List<String> strings) {
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

    public void sendMessage(ICommandSender commandSender, Enum<? extends Lang> lang, PreParseHandle handle){

    }

    private static String convertToConfigName(Enum<? extends Lang> lang) {
        return lang.name().replaceAll("_", ".");
    }
}
