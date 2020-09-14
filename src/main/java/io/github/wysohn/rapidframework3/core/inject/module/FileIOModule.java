package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.interfaces.io.file.IFileReader;
import io.github.wysohn.rapidframework3.interfaces.io.file.IFileWriter;
import io.github.wysohn.rapidframework3.utils.FileUtil;

import javax.inject.Singleton;

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
