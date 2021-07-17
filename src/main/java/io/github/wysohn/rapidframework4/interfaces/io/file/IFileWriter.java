package io.github.wysohn.rapidframework4.interfaces.io.file;

import java.io.File;
import java.io.IOException;

public interface IFileWriter {
    void accept(File file, String string) throws IOException;
}
