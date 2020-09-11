package io.github.wysohn.rapidframework3.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework2.tools.FileUtil;
import io.github.wysohn.rapidframework3.core.interfaces.io.file.IFileReader;
import io.github.wysohn.rapidframework3.core.interfaces.io.file.IFileWriter;

public class FileIOModule extends AbstractModule {
    @Provides
    IFileReader getReader() {
        return FileUtil::readFromFile;
    }

    @Provides
    IFileWriter getWriter() {
        return FileUtil::writeToFile;
    }
}
