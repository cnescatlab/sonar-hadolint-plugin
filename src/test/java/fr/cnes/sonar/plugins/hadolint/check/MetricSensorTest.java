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
package fr.cnes.sonar.plugins.hadolint.check;

import fr.cnes.sonar.plugins.hadolint.languages.DockerfileLanguage;

import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.InputFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertEquals;

public class MetricSensorTest {

    private final FileLinesContextFactory linesContextFactory = Mockito.mock(FileLinesContextFactory.class);
    private final FileLinesContext linesContext = Mockito.mock(FileLinesContext.class);
    private final String testFileDir = MetricSensorTest.class.getClassLoader().getResource("project").getFile();
    private static final String PROJECT_KEY = "ProjectKey:Dockerfile";

    @Test
    public void testDescriptor() {
        // Set descriptor and check nominal values
        SensorDescriptor descriptor = Mockito.mock(SensorDescriptor.class);
        MetricSensor sensor = new MetricSensor(this.linesContextFactory);

        sensor.describe(descriptor);
        verify(descriptor).name("fr.cnes.sonar.plugins.hadolint.check.MetricSensor");
        verify(descriptor).onlyOnLanguage(DockerfileLanguage.KEY);
    }

    @Test
    public void testNoFiles() {
        // Sensor will find no files in an empty filesystem
        DefaultFileSystem fs = new DefaultFileSystem(new File(testFileDir));
        SensorContextTester context = SensorContextTester.create(fs.baseDir());
        context.setFileSystem(fs);

        MetricSensor sensor = new MetricSensor(this.linesContextFactory);
        sensor.execute(context);

        // Check that no measures nor highlightings were generated
        assertEquals(0, context.measures(PROJECT_KEY).size());
        assertEquals(0, context.highlightingTypeAt(PROJECT_KEY, 1, 0).size());
    }

    @Test
    public void testNormalWork() throws IOException {
        // One Dockerfile will be find by sensor
        DefaultFileSystem fs = new DefaultFileSystem(new File(testFileDir));
        // Create Dockerfile for test and add it in fs
        DefaultInputFile dockerfile = TestInputFileBuilder.create(
            "ProjectKey",
            new File(""),
            new File("Dockerfile"))
            .setType(InputFile.Type.MAIN)
            .setLanguage(DockerfileLanguage.KEY)
            .setContents(new String(Files.readAllBytes(Paths.get(testFileDir + "/Dockerfile"))))
            .build();
        fs.add(dockerfile);
        
        // Init context and needed classes
        SensorContextTester context = SensorContextTester.create(fs.baseDir());
        context.setFileSystem(fs);

        Mockito.when(this.linesContextFactory.createFor(dockerfile)).thenReturn(this.linesContext);

        MetricSensor sensor = new MetricSensor(this.linesContextFactory);
        sensor.execute(context);

        // Check measures and highlightings were generated on the test file
        assertEquals(2, context.measures(PROJECT_KEY).size());
        assertEquals(1, context.highlightingTypeAt(PROJECT_KEY, 1, 0).size());
    }

    @Test
    public void testFailToReadFile() throws IOException {
        // One Dockerfile will be find by sensor but it is empty
        DefaultFileSystem fs = new DefaultFileSystem(new File(testFileDir));
        // Create Dockerfile for test and add it in fs
        DefaultInputFile dockerfile = TestInputFileBuilder.create(
            "ProjectKey",
            new File(""),
            new File("Dockerfile"))
            .setType(InputFile.Type.MAIN)
            .setLanguage(DockerfileLanguage.KEY)
            .build();
        fs.add(dockerfile);
        
        // Init context and needed classes
        SensorContextTester context = SensorContextTester.create(fs.baseDir());
        context.setFileSystem(fs);      
        
        Mockito.when(this.linesContextFactory.createFor(dockerfile)).thenReturn(this.linesContext);

        MetricSensor sensor = new MetricSensor(linesContextFactory);
        sensor.execute(context);

        // Check measures and highlightings were generated on the test file
        assertEquals(0, context.measures(PROJECT_KEY).size());
        assertEquals(0, context.highlightingTypeAt(PROJECT_KEY, 1, 0).size());
    }
    
}
