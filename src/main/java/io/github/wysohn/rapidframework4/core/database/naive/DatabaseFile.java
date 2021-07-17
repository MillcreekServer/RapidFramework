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
package io.github.wysohn.rapidframework4.core.database.naive;

import io.github.wysohn.rapidframework4.core.caching.CachedElement;
import io.github.wysohn.rapidframework4.core.database.Database;
import io.github.wysohn.rapidframework4.interfaces.io.file.IFileReader;
import io.github.wysohn.rapidframework4.interfaces.io.file.IFileWriter;
import io.github.wysohn.rapidframework4.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework4.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class DatabaseFile<K, T extends CachedElement<K>> extends Database<K, T> {
    private final ISerializer serializer;
    private final String extensionName;
    private final IFileReader fileReader;
    private final IFileWriter fileWriter;
    private final File folder;
    private final Function<String, K> strToKey;

    public DatabaseFile(ISerializer serializer,
                        String tableName,
                        Class<T> type,
                        String extensionName,
                        IFileReader fileReader,
                        IFileWriter fileWriter,
                        File folder,
                        Function<String, K> strToKey) {
        super(tableName, type);
        this.serializer = serializer;
        this.extensionName = extensionName;
        this.fileReader = fileReader;
        this.fileWriter = fileWriter;
        this.folder = folder;
        this.strToKey = strToKey;

        folder.mkdirs();
    }

    @Override
    public T load(K key) throws IOException {
        File file = new File(folder, key + "." + extensionName);
        if (!file.exists())
            return null;

        String json = fileReader.apply(file);
        if (json == null)
            return null;

        try {
            return serializer.deserializeFromString(type, json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void save(K key, T obj) throws IOException {
        File file = new File(folder, key + "." + extensionName);
        if (!file.exists())
            file.createNewFile();

        if (obj == null) {
            FileUtil.delete(file);
        } else {
            fileWriter.accept(file, serializer.serializeToString(type, obj));
        }
    }

    @Override
    public synchronized Set<K> getKeys() {
        Set<K> keys = new HashSet<>();

        Optional.ofNullable(folder.listFiles())
                .map(Arrays::stream)
                .ifPresent(fileStream -> fileStream
                        .filter(file -> file.getName().endsWith(".json"))
                        .map(File::getName)
                        .map(fileName -> fileName.substring(0, fileName.lastIndexOf('.')))
                        .map(strToKey)
                        .forEach(keys::add));

        return keys;
    }

    @Override
    public synchronized boolean has(K key) {
        String[] files = folder.list();
        if (files == null || files.length < 1)
            return false;

        for (String fileName : files) {
            if (fileName.equalsIgnoreCase(String.valueOf(key)))
                return true;
        }

        return false;
    }

    @Override
    public void clear() {
        FileUtil.delete(folder);
    }
}
