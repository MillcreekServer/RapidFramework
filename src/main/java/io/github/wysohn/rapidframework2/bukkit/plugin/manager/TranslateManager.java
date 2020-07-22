package io.github.wysohn.rapidframework2.bukkit.plugin.manager;

import io.github.wysohn.rapidframework2.bukkit.main.config.ConfigFileSession;
import io.github.wysohn.rapidframework2.bukkit.main.config.I18NConfigSession;
import io.github.wysohn.rapidframework2.bukkit.manager.api.PlaceholderAPI;
import io.github.wysohn.rapidframework2.core.main.PluginMain;

import java.io.File;

public class TranslateManager extends PluginMain.Manager {
    public static final String PREFIX = "rftrans";
    private I18NConfigSession configSession;

    public TranslateManager(int loadPriority) {
        super(loadPriority);
    }

    @Override
    public void preload() throws Exception {
        File folder = new File(main().getPluginDirectory(), "Translates");
        configSession = new I18NConfigSession(folder, "translates");
    }

    @Override
    public void enable() throws Exception {
        configSession.enable();

        main().api().getAPI(PlaceholderAPI.class).ifPresent(placeholderAPI ->
                placeholderAPI.register(PREFIX, (p, params) -> {
                    String localeCode = p.getLocale().getLanguage();
                    ConfigFileSession session = configSession.getSession(localeCode);

                    String finalParams = params.replaceAll("_", ".");
                    return String.valueOf(session.get(finalParams).orElseGet(() ->
                            configSession.DEFAULT.get(finalParams).orElse(PREFIX + "_" + params)));
                }));
    }

    @Override
    public void load() throws Exception {
        configSession.load();
    }

    @Override
    public void disable() throws Exception {
        configSession.disable();
    }
}
