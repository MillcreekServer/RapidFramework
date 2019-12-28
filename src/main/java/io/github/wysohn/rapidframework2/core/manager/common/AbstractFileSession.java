package io.github.wysohn.rapidframework2.core.manager.common;

import io.github.wysohn.rapidframework2.core.interfaces.KeyValueStorage;

import java.io.File;
import java.io.IOException;

public abstract class AbstractFileSession implements KeyValueStorage {
    protected final File file;

    public AbstractFileSession(File file){
        this.file = file;

        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        if (!file.exists()) {
            try{
                file.createNewFile();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public abstract void reload() throws IOException;

    public abstract void save() throws IOException;
}