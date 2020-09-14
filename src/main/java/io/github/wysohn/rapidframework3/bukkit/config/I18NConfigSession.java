package io.github.wysohn.rapidframework3.bukkit.config;

import io.github.wysohn.rapidframework3.interfaces.io.file.IFileWriter;
import io.github.wysohn.rapidframework3.interfaces.plugin.PluginRuntime;
import io.github.wysohn.rapidframework3.utils.Validation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class I18NConfigSession implements PluginRuntime {
    public final BukkitKeyValueStorage DEFAULT;
    private final IFileWriter writer;
    private final Map<String, BukkitKeyValueStorage> sessionMap = new HashMap<>();
    private final File folder;
    private final String fileName;

    public I18NConfigSession(IFileWriter writer, File folder, String fileName) {
        Validation.assertNotNull(folder);
        Validation.assertNotNull(fileName);
        Validation.validate(fileName, name -> name.length() > 0, "Empty fileName.");
        Validation.validate(fileName, name -> name.indexOf('.') == -1, "do not include extension.");

        this.writer = writer;
        this.folder = folder;
        this.DEFAULT = new BukkitKeyValueStorage(writer, folder, fileName);
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

                sessionMap.put(split[1], new BukkitKeyValueStorage(writer, folder, name));
            }
        }

        DEFAULT.reload();
        for (Map.Entry<String, BukkitKeyValueStorage> entry : sessionMap.entrySet()) {
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
    public BukkitKeyValueStorage getSession(String localeCode) {
        BukkitKeyValueStorage session;
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
