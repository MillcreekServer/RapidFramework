package io.github.wysohn.rapidframework4.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework4.interfaces.io.file.IFileReader;
import io.github.wysohn.rapidframework4.interfaces.io.file.IFileWriter;
import io.github.wysohn.rapidframework4.utils.FileUtil;

public class FileIOModule extends AbstractModule {
    @Provides
    @Singleton
    IFileReader getReader() {
        return FileUtil::readFromFile;
    }

    @Provides
    @Singleton
    IFileWriter getWriter() {
        return FileUtil::writeToFile;
    }
}
