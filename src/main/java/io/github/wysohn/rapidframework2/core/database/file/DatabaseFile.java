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
package io.github.wysohn.rapidframework2.core.database.file;

import io.github.wysohn.rapidframework2.core.database.Database;
import io.github.wysohn.rapidframework2.tools.FileUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * Flat file database using simple disk I/O. Not really efficient for continuous
 * read/write tasks.
 *
 * @param <T>
 * @author wysohn
 */
public class DatabaseFile<T> extends Database<T> {

    private File folder;

    public DatabaseFile(Class<T> type, File folder) {
        super(type, folder.getName());
        this.folder = folder;

        folder.mkdirs();
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized T load(String key, T def) throws IOException {
        File file = new File(folder, key);
        if (!file.exists())
            return def;

        T result = def;
        StringBuilder ser = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8.newDecoder());
             BufferedReader br = new BufferedReader(isr);) {

            String buff;
            while ((buff = br.readLine()) != null)
                ser.append(buff);
            result = deserialize(ser.toString(), type);
        }

        return result;
    }

    @Override
    public synchronized void save(String key, T value) throws IOException {
        File dest = new File(folder, key);
        if (value == null) {
            dest.delete();
            return;
        }

        String ser = serialize(value, type);
        FileUtil.writeToFile(dest, ser);

        /*
         * File file = new File(folder, key+"_tmp"); file.getParentFile().mkdirs();
         *
         * FileChannel fc = null; FileOutputStream fos = null; BufferedWriter bw = null;
         * FileLock lock = null;
         *
         * try { fos = new FileOutputStream(file); fc = fos.getChannel(); bw = new
         * BufferedWriter(new OutputStreamWriter(fos,
         * Charset.forName("UTF-8").newEncoder()));
         *
         * String ser = serialize(value, type);
         *
         * lock = fc.lock(); bw.write(ser);
         *
         * } catch (Exception e) { e.printStackTrace(); } finally { try { if(lock !=
         * null) lock.release(); } catch (IOException e) { e.printStackTrace(); }
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
         * dest.toPath(), StandardCopyOption.REPLACE_EXISTING); } catch (IOException e)
         * { e.printStackTrace(); }
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
