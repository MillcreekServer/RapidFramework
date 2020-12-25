package io.github.wysohn.rapidframework3.bukkit.config;

import io.github.wysohn.rapidframework3.core.inject.factory.IStorageFactory;
import io.github.wysohn.rapidframework3.interfaces.io.file.IFileWriter;
import io.github.wysohn.rapidframework3.interfaces.plugin.PluginRuntime;
import io.github.wysohn.rapidframework3.interfaces.store.IKeyValueStorage;
import io.github.wysohn.rapidframework3.utils.Validation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class I18NConfigSession implements PluginRuntime {
    public final IKeyValueStorage DEFAULT;
    private final IFileWriter writer;
    private final IStorageFactory storageFactory;
    private final File folder;
    private final String fileName;

    private final Map<String, IKeyValueStorage> sessionMap = new HashMap<>();

    /**
     * Create bundle of config sessions that can be differenciated depending on the locale.
     * you may use {@link #getSession(String)} to get the appropriate {@link IKeyValueStorage}
     * which is linked specifically to the locale you provided.
     *
     * @param writer         writer instance
     * @param storageFactory storage factory instance
     * @param folder         folder to save all of the yml files.
     * @param fileName       name of the file 'without' the extension. '.yml' will be automatically appended
     *                       by this class, so providing extension will result a duplicated extensions.
     */
    public I18NConfigSession(IFileWriter writer, IStorageFactory storageFactory, File folder, String fileName) {
        Validation.assertNotNull(folder);
        Validation.assertNotNull(fileName);
        Validation.validate(fileName, name -> name.length() > 0, "Empty fileName.");
        Validation.validate(fileName, name -> name.indexOf('.') == -1, "do not include extension.");

        this.writer = writer;
        this.storageFactory = storageFactory;
        this.folder = folder;
        this.DEFAULT = storageFactory.create(folder, fileName + ".yml");
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

                sessionMap.put(split[1], storageFactory.create(folder, name + ".yml"));
            }
        }

        DEFAULT.reload();
        for (Map.Entry<String, IKeyValueStorage> entry : sessionMap.entrySet()) {
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
    public IKeyValueStorage getSession(String localeCode) {
        IKeyValueStorage session;
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
