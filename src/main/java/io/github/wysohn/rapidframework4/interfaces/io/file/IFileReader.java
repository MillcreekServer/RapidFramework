package io.github.wysohn.rapidframework4.interfaces.io.file;

import java.io.File;
import java.io.IOException;

public interface IFileReader {
    String apply(File file) throws IOException;
}
