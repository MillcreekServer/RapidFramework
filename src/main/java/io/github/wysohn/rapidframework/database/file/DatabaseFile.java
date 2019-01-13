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
package io.github.wysohn.rapidframework.database.file;

import io.github.wysohn.rapidframework.database.Database;
import io.github.wysohn.rapidframework.utils.files.FileUtil;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

/**
 * Flat file database using simple disk I/O. Not really efficient for continuous
 * read/write tasks.
 * 
 * @author wysohn
 *
 * @param <T>
 */
public class DatabaseFile<T> extends Database<T> {
    private final Type type;
    private File folder;

    public DatabaseFile(File folder, Type type) {
        this.folder = folder;
        this.type = type;

        folder.mkdirs();
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized T load(String key, T def) {
        File file = new File(folder, key);
        if (!file.exists())
            return def;

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        T result = def;
        String ser = "";
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, Charset.forName("UTF-8").newDecoder());
            br = new BufferedReader(isr);

            String buff;
            while ((buff = br.readLine()) != null)
                ser += buff;
            result = (T) deserialize(ser, type);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (isr != null)
                    isr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    public synchronized void save(String key, T value) {
        File dest = new File(folder, key);
        if (value == null) {
            dest.delete();
            return;
        }

        String ser = serialize(value, type);

        try {
            FileUtil.writeToFile(dest, ser);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
         * File file = new File(folder, key+"_tmp");
         * file.getParentFile().mkdirs();
         * 
         * FileChannel fc = null; FileOutputStream fos = null; BufferedWriter bw
         * = null; FileLock lock = null;
         * 
         * try { fos = new FileOutputStream(file); fc = fos.getChannel(); bw =
         * new BufferedWriter(new OutputStreamWriter(fos,
         * Charset.forName("UTF-8").newEncoder()));
         * 
         * String ser = serialize(value, type);
         * 
         * lock = fc.lock(); bw.write(ser);
         * 
         * } catch (Exception e) { e.printStackTrace(); } finally { try {
         * if(lock != null) lock.release(); } catch (IOException e) {
         * e.printStackTrace(); }
         * 
         * try { if(bw != null) bw.close(); } catch (IOException e) {
         * e.printStackTrace(); }
         * 
         * try { if(fc != null) fc.close(); } catch (IOException e) {
         * e.printStackTrace(); }
         * 
         * try { if(fos != null) fos.close(); } catch (IOException e) {
         * e.printStackTrace(); }
         * 
         * try { if(file != null && dest != null) Files.move(file.toPath(),
         * dest.toPath(), StandardCopyOption.REPLACE_EXISTING); } catch
         * (IOException e) { e.printStackTrace(); }
         * 
         * file.delete(); }
         */
    }

    @Override
    public synchronized Set<String> getKeys() {
        Set<String> keys = new HashSet<String>();

        for (File file : folder.listFiles()) {
            keys.add(file.getName());
        }

        return keys;
    }

    @Override
    public synchronized boolean has(String key) {
        for (String fileName : folder.list()) {
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
