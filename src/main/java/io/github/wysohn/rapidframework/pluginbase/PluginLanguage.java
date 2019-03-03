/*******************************************************************************
 *     Copyright (C) 2017 wysohn
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package io.github.wysohn.rapidframework.pluginbase;

import io.github.wysohn.rapidframework.main.FakePlugin;
import io.github.wysohn.rapidframework.pluginbase.language.DefaultLanguages;
import io.github.wysohn.rapidframework.utils.files.JarUtil;
import io.github.wysohn.rapidframework.utils.files.JarUtil.CopyOption;
import io.github.wysohn.rapidframework.utils.serializations.Utf8YamlConfiguration;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

public final class PluginLanguage implements PluginProcedure {
    private final String defaultLang;
    private final Set<String> supportLanguages;
    private final Map<String, LanguageFileSession> langFiles = new HashMap<String, LanguageFileSession>();
    private final Set<Language> languages = new HashSet<Language>() {
	{
	    for (Language lang : DefaultLanguages.values())
		add(lang);
	}
    };

    private PluginBase base;

    private String command = null;
    private Queue<Double> doub = new LinkedList<Double>();
    private Queue<Integer> integer = new LinkedList<Integer>();
    private Queue<Long> llong = new LinkedList<Long>();
    private Queue<String> string = new LinkedList<String>();
    private Queue<Boolean> bool = new LinkedList<Boolean>();

    public PluginLanguage(Set<String> supportLanguages, String defaultLang) {
	Validate.notNull(supportLanguages);
	Validate.notNull(defaultLang);

	this.supportLanguages = supportLanguages;
	this.defaultLang = defaultLang;
    }

    public PluginLanguage(Set<String> supportLanguages) {
	this(supportLanguages, Locale.ENGLISH.toString());
    }

    @Override
    public void onEnable(final PluginBase base) throws Exception {
	this.base = base;

	JarUtil.copyFolderFromJar("lang", base.getDataFolder(), CopyOption.COPY_IF_NOT_EXIST);
	File langFolder = new File(base.getDataFolder(), "lang");
	if (!langFolder.exists())
	    langFolder.mkdirs();

	for (String lang : supportLanguages) {
	    File file = new File(langFolder, lang + ".yml");
	    LanguageFileSession session = null;

	    try {
		session = new LanguageFileSession(file);
	    } catch (IOException e) {
		base.getLogger().severe("While creating file [" + lang + ".yml]: ");
		base.getLogger().severe(e.getMessage());
	    }

	    langFiles.put(lang, session);
	}

	if (!langFiles.containsKey(defaultLang)) {
	    throw new Exception("default language file[" + defaultLang + ".yml] doesn't exist!");
	}

	for (Entry<String, LanguageFileSession> entry : langFiles.entrySet()) {
	    fillIfEmpty(entry);
	}
    }

    @Override
    public void onDisable(PluginBase base) throws Exception {
	// save();
    }

    @Override
    public void onReload(PluginBase base) throws Exception {
	reload();
    }

    private void save() throws IOException {
	for (Entry<String, LanguageFileSession> entry : langFiles.entrySet()) {
	    entry.getValue().save();
	}
    }

    private void reload() throws FileNotFoundException, IOException, InvalidConfigurationException {
	for (Entry<String, LanguageFileSession> entry : langFiles.entrySet()) {
	    base.getLogger().info("reloading language [" + entry.getKey() + "]...");
	    entry.getValue().reload();
	    fillIfEmpty(entry);
	    base.getLogger().info("loaded!");
	}
    }

    /**
     *
     * @param lang
     * @return true if there was no same Language already registered
     */
    public boolean registerLanguage(Language lang) {
	return languages.add(lang);
    }

    private void fillIfEmpty(Entry<String, LanguageFileSession> entry) {
	String locale = entry.getKey();
	LanguageFileSession session = entry.getValue();

	int i = 0, old = 0;
	for (final Language lang : languages) {
	    String str = convertToConfigName(lang.toString());

	    List<String> oldStyle = (List<String>) session.config.get(lang.toString());
	    session.config.set(lang.toString(), null);
	    if (session.config.get(str) == null) {
		// old styled config to new style
		if (oldStyle != null) {
		    session.config.set(str, oldStyle);
		    old++;
		} else {
		    session.config.set(str, new ArrayList<String>() {
			{
			    addAll(Arrays.asList(lang.getEngDefault()));
			}
		    });
		    i++;
		}
	    }

	    try {
		session.save();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	base.getLogger().info("Converted [" + old + "] old style langauges of [" + locale + "] locale.");
	base.getLogger().info("Added [" + i + "] new langauges to [" + locale + "] locale.");
    }

    public void setCommand(String command) {
	this.command = command;
    }

    public void addDouble(double doub) {
	this.doub.add(Double.valueOf(doub));
    }

    public void addInteger(int integer) {
	this.integer.add(Integer.valueOf(integer));
    }

    public void addString(String string) {
	Validate.notNull(string);

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

    public String parseFirstString(Language lang) {
	String[] result = parseStrings(null, lang, null);
	return result.length < 1 ? "NULL" : result[0];
    }

    public String parseFirstString(Object sender, Language lang) {
	String locale = null;
	String[] result = new String[0];

	try {
	    if (sender instanceof Player) {
		locale = FakePlugin.nmsEntityManager.getLocale((Player) sender);
	    }
	} catch (Exception e) {
	    // silently fail
	} finally {
	    result = parseStrings(sender, lang, locale);
	}

	return result.length < 1 ? "NULL" : result[0];
    }

    public String parseFirstString(Object sender, Locale locale, Language lang) {
	String[] result = parseStrings(sender, lang, locale.toString());
	return result.length < 1 ? "NULL" : result[0];
    }

    public String parseFirstString(Object sender, Language lang, String locale) {
	String[] result = parseStrings(sender, lang, locale);
	return result.length < 1 ? "NULL" : result[0];
    }

    public String[] parseStrings(Language lang) {
	return parseStrings(null, lang, null);
    }

    public String[] parseStrings(Object sender, Language lang) {
	String locale = null;
	try {
	    if (sender instanceof Player)
		locale = FakePlugin.nmsEntityManager.getLocale((Player) sender);
	} catch (Exception e) {
	    // silently fail
	}

	return parseStrings(sender, lang, locale);
    }

    public String[] parseStrings(Object sender, Locale locale, Language lang) {
	return parseStrings(sender, lang, locale.toString());
    }

    private static String convertToConfigName(String langName) {
	return langName.replaceAll("_", ".");
    }
    /*
     * private static String converToFieldName(String configKey){ return
     * configKey.replaceAll("\\.", "_"); }
     */

    @SuppressWarnings("unchecked")
    public String[] parseStrings(Object sender, Language lang, String locale) {
	if (locale == null)
	    locale = defaultLang;

	Validate.notNull(lang);

	LanguageFileSession session = langFiles.get(locale);
	if (session == null)
	    session = langFiles.get(defaultLang);

	if (session == null) {
	    base.getLogger().severe("Cannot parse language with locale " + locale + "!");
	    return new String[] {};
	}

	List<String> str = new ArrayList<String>();
	List<String> read = (List<String>) session.config.get(convertToConfigName(lang.toString()));
	if (read != null)
	    str.addAll(read);

	Validate.notNull(str);

	replaceVariables(sender, str);

	this.command = null;
	this.doub.clear();
	this.integer.clear();
	this.string.clear();
	this.bool.clear();
	this.llong.clear();

	return str.toArray(new String[str.size()]);
    }

    /**
     * @author Hex_27
     * @param msg
     * @return
     */
    public String colorize(String msg) {
//        String coloredMsg = "";
//        for (int i = 0; i < msg.length(); i++) {
//            if (msg.charAt(i) == '&')
//                coloredMsg += '§';// §
//            else
//                coloredMsg += msg.charAt(i);
//        }
	return ChatColor.translateAlternateColorCodes('&', msg);
    }

    private void replaceVariables(Object sender, List<String> strings) {
	for (int i = 0; i < strings.size(); i++) {
	    String str = strings.get(i);
	    if (str == null)
		continue;

	    str = colorize(str);

	    if (str.contains("${")) {
		int start = -1;
		int end = -1;

		while (!((start = str.indexOf("${")) == -1 || (end = str.indexOf("}")) == -1)) {

		    String leftStr = str.substring(0, start);
		    String rightStr = str.substring(end + 1, str.length());

		    String varName = str.substring(start + 2, end);

		    switch (varName) {
		    case "double":
			String form = "###,###,###.";
			for (int n = 0; n < base.getPluginConfig().Languages_Double_DecimalPoints; n++)
			    form += "#";
			DecimalFormat format = new DecimalFormat(form);
			Double val = this.doub.poll();
			String msg = null;
			if (val != null) {
			    if (Double.isFinite(val))
				msg = format.format(val);
			    else
				msg = "NaN";
			}
			str = leftStr + String.valueOf(msg) + rightStr;
			break;
		    case "integer":
			str = leftStr + String.valueOf(this.integer.poll()) + rightStr;
			break;
		    case "long":
			str = leftStr + String.valueOf(this.llong.poll()) + rightStr;
			break;
		    case "string":
			str = leftStr + String.valueOf(this.string.poll()) + rightStr;
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
		    case "player":
			str = leftStr + (sender instanceof Player ? ((Player) sender).getName() : "null") + rightStr;
			break;
		    case "dbtype":
			String dbTypes = "[";
			for (Entry<Class<? extends PluginManager>, PluginManager> entry : base.pluginManagers
				.entrySet()) {
			    Set<String> types = entry.getValue().getValidDBTypes();
			    if (types == null)
				continue;

			    dbTypes += entry.getKey().getSimpleName() + "@";
			    for (String type : types) {
				dbTypes += type + ",";
			    }
			    dbTypes += entry.getKey().getSimpleName() + " ";
			}
			dbTypes += "]";

			str = leftStr + dbTypes + rightStr;
			break;
		    case "command":
			str = leftStr + String.valueOf(command) + rightStr;
			break;
		    default:
			str = leftStr + String.valueOf("?") + rightStr;
			break;
		    }
		}
	    }

	    strings.set(i, str);
	}
    }

    /**
     * Create enum class and implement this interface. See {@link DefaultLanguages}
     * for example. It is also expected to get registered in {@link PluginLanguage}
     * using registerLanguage() method.
     *
     * @author wysohn
     *
     */
    public interface Language {
	public String[] getEngDefault();
    }

    @FunctionalInterface
    public interface PreParseHandle {
	void onParse(PluginLanguage lang, Player player);
    }

    private class LanguageFileSession {
	private File file;
	FileConfiguration config;

	public LanguageFileSession(File file) throws FileNotFoundException, IOException, InvalidConfigurationException {
	    this.file = file;

	    if (!file.exists()) {
		file.createNewFile();
	    }

	    this.config = new Utf8YamlConfiguration();

	    this.config.load(file);
	}

	public void reload() throws FileNotFoundException, IOException, InvalidConfigurationException {
	    this.config.load(file);
	}

	public void save() throws IOException {
	    this.config.save(file);
	}
    }

}
