/*******************************************************************************
 *     Copyright (C) 2017 wysohn
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package io.github.wysohn.rapidframework3.core.database;

import io.github.wysohn.rapidframework2.tools.FileUtil;
import io.github.wysohn.rapidframework3.core.interfaces.io.file.IFileReader;
import io.github.wysohn.rapidframework3.core.interfaces.io.file.IFileWriter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DatabaseFile extends Database {
    private final String extensionName;
    private final IFileReader fileReader;
    private final IFileWriter fileWriter;
    private final File folder;

    public DatabaseFile(String tableName,
                        String extensionName,
                        IFileReader fileReader,
                        IFileWriter fileWriter,
                        File folder) {
        super(tableName);
        this.extensionName = extensionName;
        this.fileReader = fileReader;
        this.fileWriter = fileWriter;
        this.folder = folder;

        folder.mkdirs();
    }

    @Override
    public String load(String key) throws IOException {
        File file = new File(folder, key + "." + extensionName);
        if (!file.exists())
            return null;

        return fileReader.apply(file);
    }

    @Override
    public void save(String key, String serialized) throws IOException {
        File file = new File(folder, key + "." + extensionName);
        if (!file.exists())
            file.createNewFile();

        fileWriter.accept(file, serialized);
    }

    @Override
    public synchronized Set<String> getKeys() {
        Set<String> keys = new HashSet<String>();

        Optional.ofNullable(folder.listFiles())
                .map(Arrays::stream)
                .ifPresent(fileStream -> fileStream
                        .filter(file -> file.getName().endsWith(".json"))
                        .map(File::getName)
                        .map(fileName -> fileName.substring(0, fileName.lastIndexOf('.')))
                        .forEach(keys::add));

        return keys;
    }

    @Override
    public synchronized boolean has(String key) {
        String[] files = folder.list();
        if (files == null || files.length < 1)
            return false;

        for (String fileName : files) {
            if (fileName.equalsIgnoreCase(key))
                return true;
        }

        return false;
    }

    @Override
    public void clear() {
        FileUtil.delete(folder);
    }
}
