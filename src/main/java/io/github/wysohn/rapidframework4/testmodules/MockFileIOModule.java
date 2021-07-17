package io.github.wysohn.rapidframework4.testmodules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework4.interfaces.io.file.IFileReader;
import io.github.wysohn.rapidframework4.interfaces.io.file.IFileWriter;

public class MockFileIOModule extends AbstractModule {
    private final IFileReader mockFileReader;
    private final IFileWriter mockFileWriter;

    public MockFileIOModule(IFileReader mockFileReader, IFileWriter mockFileWriter) {
        this.mockFileReader = mockFileReader;
        this.mockFileWriter = mockFileWriter;
    }

    @Provides
    IFileReader getReader() {
        return mockFileReader;
    }

    @Provides
    IFileWriter getWriter() {
        return mockFileWriter;
    }
}
