package util;

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

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarUtil {
    public static final char JAR_SEPARATOR = '/';

    public static void copyFromJar(String glob, File destFolder, CopyOption option) throws IOException {
        copyFromJar(glob, destFolder, option, null);
    }

    public static void copyFromJar(String glob, File destFolder, CopyOption option, PathTrimmer trimmer)
            throws IOException {
        copyFromJar(JarUtil.class, glob, destFolder, option, trimmer);
    }

    public static void copyFromJar(Class<?> clazz, String glob, File destFolder, CopyOption option) throws IOException {
        copyFromJar(clazz, glob, destFolder, option, null);
    }

    /**
     * @param clazz
     * @param glob       refer to {@link FilenameUtils#wildcardMatch(String, String)}
     * @param destFolder
     * @param option
     * @param trimmer
     * @throws IOException
     */
    public static void copyFromJar(Class<?> clazz, String glob, File destFolder, CopyOption option, PathTrimmer trimmer)
            throws IOException {
        if (!destFolder.exists())
            destFolder.mkdirs();

        byte[] buffer = new byte[1024];

        Optional<String> optFullPath = Optional.of(clazz)
                .map(Class::getProtectionDomain)
                .map(ProtectionDomain::getCodeSource)
                .map(CodeSource::getLocation)
                .map(URL::getPath);

        if (!optFullPath.isPresent())
            return; // can't do much in this case

        String path = optFullPath.get();
        if (trimmer != null)
            path = trimmer.trim(path);
        String decodedPath = URLDecoder.decode(path, "UTF-8").replace(" ", "%20");

        URI fullPath = null;
        try {
            if (!decodedPath.startsWith("file"))
                decodedPath = "file://" + decodedPath;
            fullPath = new URI(decodedPath);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        File target = new File(fullPath);
        if (!target.isFile())
            return; // probably the case when it's not called via java -jar

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(target))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();

                if (!FilenameUtils.wildcardMatchOnSystem(fileName, glob)) {
                    continue;
                }

                // folder
                if (fileName.charAt(fileName.length() - 1) == JAR_SEPARATOR) {
                    File file = new File(destFolder + File.separator + fileName);
                    if (file.isFile()) {
                        file.delete();
                    }
                    file.mkdirs();
                    continue;
                }

                // duplicate
                File file = new File(destFolder + File.separator + fileName);
                if (option == CopyOption.COPY_IF_NOT_EXIST && file.exists() && file.length() > 0L)
                    continue;

                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();

                if (!file.exists())
                    file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
        }
    }

    public static void main(String[] ar) throws IOException {
        File folder = new File("build/tmp");

        copyFromJar(JarUtil.class, "src/main/java/util/ISO*.java", folder, CopyOption.REPLACE_IF_EXIST);
    }

    public enum CopyOption {
        COPY_IF_NOT_EXIST, REPLACE_IF_EXIST
    }

    @FunctionalInterface
    public interface PathTrimmer {
        String trim(String original);
    }
}