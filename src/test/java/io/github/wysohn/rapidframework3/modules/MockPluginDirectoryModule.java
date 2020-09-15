package io.github.wysohn.rapidframework3.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;

import java.io.File;

public class MockPluginDirectoryModule extends AbstractModule {
    @Provides
    @PluginDirectory
    File getDirectory() {
        File folder = new File("build/tmp/plugindir/");
        folder.mkdirs();
        return folder;
    }
}
