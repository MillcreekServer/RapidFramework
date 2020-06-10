package io.github.wysohn.rapidframework2.bukkit.plugin.manager;

import io.github.wysohn.rapidframework2.bukkit.main.ConfigFileSession;
import io.github.wysohn.rapidframework2.bukkit.manager.api.PlaceholderAPI;
import io.github.wysohn.rapidframework2.core.main.PluginMain;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TranslateManager extends PluginMain.Manager {
    private final Map<String, ConfigFileSession> sessionMap = new HashMap<>();
    private File folder;
    private ConfigFileSession defaultSession;

    public TranslateManager(int loadPriority) {
        super(loadPriority);
        this.folder = new File(main().getPluginDirectory(), "Translates");
        this.defaultSession = new ConfigFileSession(new File(folder, "translates.yml"));
    }

    @Override
    public void enable() throws Exception {
        main().api().getAPI(PlaceholderAPI.class).ifPresent(placeholderAPI ->
                placeholderAPI.register("rftrans", (p, params) -> {
                    String localeCode = p.getLocale().getLanguage();
                    ConfigFileSession session = getSession(localeCode);

                    String finalParams = params.replaceAll("_", ".");
                    return String.valueOf(session.get(finalParams).orElseGet(() ->
                            defaultSession.get(finalParams).orElse(null)));
                }));
    }

    private ConfigFileSession getSession(String localeCode) {
        ConfigFileSession session;
        synchronized (sessionMap) {
            session = sessionMap.get(localeCode);
        }

        if (session == null) {
            File file = new File(folder, "translates_" + localeCode + ".yml");
            if (!file.exists()) {
                defaultSession.save(file);
            } else {

            }

            return defaultSession;
        } else {
            return session;
        }
    }

    @Override
    public void load() throws Exception {
        defaultSession.reload();

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
}
