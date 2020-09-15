package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;

import java.io.File;

public class PluginDirectoryModule extends AbstractModule {
    private final File directory;

    public PluginDirectoryModule(File directory) {
        this.directory = directory;
    }

    @Provides
    @PluginDirectory
    public File getDirectory() {
        return directory;
    }
}
