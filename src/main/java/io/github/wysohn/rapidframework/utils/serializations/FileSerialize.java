/*
 * Copyright (C) 2015, 2017 wysohn.  All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,  version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.wysohn.rapidframework.utils.serializations;

import io.github.wysohn.rapidframework.utils.serializations.exceptions.FileSerializeException;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public abstract class FileSerialize {
    private File dbPath;
    protected Utf8YamlConfiguration db;

    /**
     * @param folder   folder
     * @param fileName fileName
     * @throws FileSerializeException
     */
    public FileSerialize(File folder, String fileName) throws FileSerializeException {
        if (!folder.exists())
            folder.mkdirs();

        dbPath = new File(folder, fileName);
        if (!dbPath.exists())
            try {
                Bukkit.getLogger().info("Creating file " + fileName);
                dbPath.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().warning("Failed to create file " + fileName);
                throw new FileSerializeException("Failed to create file " + fileName);
            }

        db = new Utf8YamlConfiguration();
        try {
            db.load(dbPath);
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getLogger().warning("Failed to load " + fileName);
            e.printStackTrace();
            throw new FileSerializeException("Failed to load " + fileName);
        }

        if (db == null) {
            Bukkit.getLogger().warning("Failed to load " + fileName);
            throw new FileSerializeException("Failed to load " + fileName);
        }

    }

    protected boolean deleteFile() {
        db = null;
        return dbPath.delete();
    }

    protected void clearAll() throws FileSerializeException {
        // Bukkit.getLogger().info(db.getValues(false).size()+" is size.");
        for (Map.Entry<String, Object> entry : db.getValues(false).entrySet()) {
            db.set(entry.getKey(), null);
            // Bukkit.getLogger().info(entry.getKey()+" cleared.");
        }
    }

    protected void saveAll() throws FileSerializeException {
        try {
            db.save(dbPath);
        } catch (IOException e) {
            Bukkit.getLogger().warning("saveAll() failed");
            throw new FileSerializeException("saveAll() failed");
        }
    }

    protected void save(String key, Object val) throws FileSerializeException {
        Validate.notNull(key);

        db.set(key, val);

        try {
            db.save(dbPath);
        } catch (IOException e) {
            Bukkit.getLogger().warning("saveAll() failed");
            throw new FileSerializeException("saveAll() failed");
        }
    }

    protected Object load(String key) {
        return db.get(key);
    }

    protected Object load(String key, Object def) {
        return db.get(key, def);
    }

    public void reload() {
        try {
            db.load(dbPath);
        } catch (IOException | InvalidConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
