package io.github.wysohn.rapidframework3.bukkit.plugin.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework3.bukkit.config.I18NConfigSession;
import io.github.wysohn.rapidframework3.bukkit.manager.api.PlaceholderAPI;
import io.github.wysohn.rapidframework3.core.api.ManagerExternalAPI;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;
import io.github.wysohn.rapidframework3.core.inject.factory.IStorageFactory;
import io.github.wysohn.rapidframework3.core.main.Manager;
import io.github.wysohn.rapidframework3.interfaces.io.file.IFileWriter;
import io.github.wysohn.rapidframework3.interfaces.store.IKeyValueStorage;

import java.io.File;

@Singleton
public class TranslateManager extends Manager {
    public static final String PREFIX = "rftrans";

    private final File pluginDir;
    private final ManagerExternalAPI apiManager;
    private final IFileWriter fileWriter;
    private final IStorageFactory storageFactory;

    private I18NConfigSession configSession;

    @Inject
    public TranslateManager(@PluginDirectory File pluginDir,
                            ManagerExternalAPI apiManager,
                            IFileWriter fileWriter,
                            IStorageFactory storageFactory) {
        this.pluginDir = pluginDir;
        this.apiManager = apiManager;
        this.fileWriter = fileWriter;
        this.storageFactory = storageFactory;
    }

    @Override
    public void preload() throws Exception {
        File folder = new File(pluginDir, "Translates");
        configSession = new I18NConfigSession(fileWriter, storageFactory, folder, "translates");
    }

    @Override
    public void enable() throws Exception {
        configSession.enable();

        apiManager.getAPI(PlaceholderAPI.class).ifPresent(placeholderAPI ->
                placeholderAPI.register(PREFIX, (p, params) -> {
                    String localeCode = p.getLocale().getLanguage();
                    IKeyValueStorage session = configSession.getSession(localeCode);

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
