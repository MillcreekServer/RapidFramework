package io.github.wysohn.rapidframework3.bukkit.plugin.manager;

import io.github.wysohn.rapidframework3.bukkit.config.BukkitKeyValueStorage;
import io.github.wysohn.rapidframework3.bukkit.config.I18NConfigSession;
import io.github.wysohn.rapidframework3.bukkit.manager.api.PlaceholderAPI;
import io.github.wysohn.rapidframework3.core.main.Manager;
import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.interfaces.io.file.IFileWriter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;

@Singleton
public class TranslateManager extends Manager {
    public static final String PREFIX = "rftrans";

    private final IFileWriter fileWriter;

    private I18NConfigSession configSession;

    @Inject
    public TranslateManager(PluginMain main, IFileWriter fileWriter) {
        super(main);
        this.fileWriter = fileWriter;
    }

    @Override
    public void preload() throws Exception {
        File folder = new File(main().getPluginDirectory(), "Translates");
        configSession = new I18NConfigSession(fileWriter, folder, "translates");
    }

    @Override
    public void enable() throws Exception {
        configSession.enable();

        main().api().getAPI(PlaceholderAPI.class).ifPresent(placeholderAPI ->
                placeholderAPI.register(PREFIX, (p, params) -> {
                    String localeCode = p.getLocale().getLanguage();
                    BukkitKeyValueStorage session = configSession.getSession(localeCode);

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
