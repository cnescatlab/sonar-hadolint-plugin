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

import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.log.LogTester;
import org.sonar.api.utils.log.LoggerLevel;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class FileSystemTest {
    @Rule
    public LogTester logTester = new LogTester();

    @Test
    public void testConstructor() throws NoSuchFieldException, IllegalAccessException, URISyntaxException, IOException {
        Field defaultFS = FileSystem.class.getDeclaredField("defaultFileSystem");
        defaultFS.setAccessible(true);
        Field fsField = FileSystem.class.getDeclaredField("fs");
        fsField.setAccessible(true);

        // Test with an existing, non-default provider
        FileSystem fs = new FileSystem(new URI("jrt:/"));
        assertFalse((Boolean)defaultFS.get(fs));
        assertNotEquals(FileSystems.getDefault(), fsField.get(fs));
        fs.close();

        // Test with an invalid URI
        fs = new FileSystem(Paths.get("src").toUri());
        assertEquals(1, logTester.logs(LoggerLevel.WARN).size());
        assertTrue(logTester.logs(LoggerLevel.WARN).get(0).startsWith("Using default FS because of an error: "));
        assertTrue((Boolean)defaultFS.get(fs));
        assertEquals(FileSystems.getDefault(), fsField.get(fs));

        logTester.clear();
        FileSystem fs2 = new FileSystem(new URI("file:///"));
        assertEquals(1, logTester.logs(LoggerLevel.WARN).size());
        assertTrue(logTester.logs(LoggerLevel.WARN).get(0).startsWith("FS already exists for URI: "));
        assertEquals(fsField.get(fs), fsField.get(fs2));
    }

    @Test
    public void testReadDirectory() throws URISyntaxException, IOException {
        FileSystem fs = new FileSystem(new URI("file:///"));
        Path root = Paths.get("src", "test", "resources", "rules", "hadolint");
        List<Path> content = fs.readDirectory(root.toUri()).collect(Collectors.toList());
        assertEquals(3, content.size());
        assertTrue(content.contains(root.resolve("DL0000.html").toAbsolutePath()));
        assertTrue(content.contains(root.resolve("DL0000.json").toAbsolutePath()));
        assertTrue(content.contains(root.resolve("DL0001.json").toAbsolutePath()));
    }    
}
