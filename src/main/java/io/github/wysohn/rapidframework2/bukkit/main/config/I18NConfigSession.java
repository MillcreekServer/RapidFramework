package io.github.wysohn.rapidframework2.bukkit.main.config;

import io.github.wysohn.rapidframework2.core.interfaces.plugin.PluginRuntime;
import util.Validation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class I18NConfigSession implements PluginRuntime {
    public final ConfigFileSession DEFAULT;
    private final Map<String, ConfigFileSession> sessionMap = new HashMap<>();
    private final File folder;
    private final String fileName;

    public I18NConfigSession(File folder, String fileName) {
        Validation.assertNotNull(folder);
        Validation.assertNotNull(fileName);
        Validation.validate(fileName, name -> name.length() > 0, "Empty fileName.");
        Validation.validate(fileName, name -> name.indexOf('.') == -1, "do not include extension.");

        this.folder = folder;
        this.DEFAULT = new ConfigFileSession(new File(folder, fileName + ".yml"));
        this.fileName = fileName;
    }

    @Override
    public void enable() throws Exception {

    }

    @Override
    public void load() throws Exception {
        File[] files = folder.listFiles(f -> f.getName().endsWith(".yml")
                && f.getName().contains("_")
                && f.getName().split("_", 2)[0].equals(fileName));
        if (files != null) {
            for (File file : files) {
                String name = file.getName().substring(0, file.getName().indexOf('.'));
                String[] split = name.split("_", 2);
                if (split.length < 2)
                    continue;

                sessionMap.put(split[1], new ConfigFileSession(file));
            }
        }

        DEFAULT.reload();
        for (Map.Entry<String, ConfigFileSession> entry : sessionMap.entrySet()) {
            try {
                entry.getValue().reload();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void disable() throws Exception {

    }

    /**
     * @param localeCode {@link java.util.Locale#getLanguage()}
     * @return
     */
    public ConfigFileSession getSession(String localeCode) {
        ConfigFileSession session;
        synchronized (sessionMap) {
            session = sessionMap.get(localeCode);
        }

        if (session == null) {
            return DEFAULT;
        } else {
            return session;
        }
    }

    public Set<String> getLocales() {
        return sessionMap.keySet();
    }
}
