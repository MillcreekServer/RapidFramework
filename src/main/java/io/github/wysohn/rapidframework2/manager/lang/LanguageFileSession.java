package io.github.wysohn.rapidframework2.manager.lang;

import java.io.File;
import java.io.IOException;

public abstract class LanguageFileSession {
    protected final File file;

    public LanguageFileSession(File file) throws IOException{
        this.file = file;

        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public abstract void reload() throws IOException;

    public abstract void save() throws IOException;
}