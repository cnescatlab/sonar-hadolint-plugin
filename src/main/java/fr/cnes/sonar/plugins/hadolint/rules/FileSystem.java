/*
 * This file is part of sonar-hadolint-plugin.
 *
 * sonar-hadolint-plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sonar-hadolint-plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with sonar-hadolint-plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.cnes.sonar.plugins.hadolint.rules;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * A class used to make the default {@code FileSystem} be closable like the other file systems without raising an exception
 */
public final class FileSystem implements Closeable {
    private static final Logger LOGGER = Loggers.get(FileSystem.class);

    private boolean defaultFileSystem = false;
    private java.nio.file.FileSystem fs;


    /**
     * Initializes a new file system connected to the passed resource
     *
     * @param uri the URI to locate the file system
     */
    public FileSystem(URI uri) {
        try {
            fs = FileSystems.newFileSystem(uri, Collections.emptyMap(), null);
        } catch (IOException|IllegalArgumentException e) {
            LOGGER.warn("Using default FS because of an error: {}", e.getMessage());
            fs = FileSystems.getDefault();
            defaultFileSystem = true;
        } catch (FileSystemAlreadyExistsException e) {
            LOGGER.warn("FS already exists for URI: {}", uri.toString());
            fs = FileSystems.getFileSystem(uri);
            defaultFileSystem = fs == FileSystems.getDefault();
        }
    }


    @Override
    public void close() throws IOException {
        // The default file system is actually not closeable, see https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html
        if (!defaultFileSystem) {
            fs.close();
        }
    }

    /**
     * Reads and returns the 1st-level content of the passed directory described as a URI. This URI must be compatible
     * with the file system.
     *
     * @param uri an URI to a directory
     * @return returns the 1st-level content of the passed directory described as a URI
     * @throws IOException if an I/O error occurs
     * @throws NotDirectoryException if the file could not otherwise be opened because it is not a directory (optional specific exception)
     * @throws SecurityException In the case the default provider is used, and a security manager is installed, the checkRead
     *                           method is invoked to check read access to the directory.
     */
    public Stream<Path> readDirectory(URI uri) throws IOException {
        List<Path> paths = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(fs.provider().getPath(uri))) {
            for (Path entry: stream) {
                paths.add(entry);
            }
            return paths.stream();
        }
    }
}