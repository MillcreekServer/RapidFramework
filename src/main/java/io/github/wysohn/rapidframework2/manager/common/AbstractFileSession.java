package io.github.wysohn.rapidframework2.manager.common;

import java.io.File;
import java.io.IOException;

public abstract class AbstractFileSession implements KeyValueStorage {
    protected final File file;

    public AbstractFileSession(File file) throws IOException{
        this.file = file;

        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public abstract void reload() throws IOException;

    public abstract void save() throws IOException;
}