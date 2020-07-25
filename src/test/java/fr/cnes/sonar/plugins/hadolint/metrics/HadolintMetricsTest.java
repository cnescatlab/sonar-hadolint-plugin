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
package fr.cnes.sonar.plugins.hadolint.metrics;

import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class HadolintMetricsTest {
    @Test
    public void testMetricsAreAllGood() throws IOException {
        // Set up InputFile and Context to be able to create measures
        String testFileDir = HadolintMetricsTest.class.getClassLoader().getResource("project").getFile();
        String testFilePath = testFileDir + "/Dockerfile";
        DefaultInputFile testFile = TestInputFileBuilder
                .create("ProjectKey", new File(testFileDir), new File(testFilePath))
                .setLanguage("Dockerfile")
                .setContents(new String(Files.readAllBytes(Paths.get(testFilePath))))
                .initMetadata("public class Foo {\n}").build();
        SensorContextTester context = SensorContextTester.create(new File(testFileDir));
        context.fileSystem().add(testFile);
        List<InputFile> inputFiles = new ArrayList<>();
        inputFiles.add(testFile);
        
        // Generate measures
        DockerfileMetrics metrics = new DockerfileMetrics(context, inputFiles);
        metrics.analyse();
        String componentKey = "ProjectKey:Dockerfile";

        // Check the measures are OK
        assertEquals(2, context.measures(componentKey).size());
        assertEquals(5, context.measure(componentKey, CoreMetrics.NCLOC).value().intValue());
        assertEquals(2, context.measure(componentKey, CoreMetrics.COMMENT_LINES).value().intValue());
    }
    
    @Test
    public void testWhenFileDoesNotExist() {
        // Set up InputFile and Context to be able to create measures
        String testFileDir = HadolintMetricsTest.class.getClassLoader().getResource("project").getFile();
        String testFilePath = testFileDir + "/file-not-exist";
        DefaultInputFile testFile = TestInputFileBuilder
                .create("ProjectKey", new File(testFileDir), new File(testFilePath))
                .build();
        SensorContextTester context = SensorContextTester.create(new File(testFileDir));
        context.fileSystem().add(testFile);
        List<InputFile> inputFiles = new ArrayList<>();
        inputFiles.add(testFile);
        
        // Generate measures
        DockerfileMetrics metrics = new DockerfileMetrics(context, inputFiles);
        metrics.analyse();
        String componentKey = "ProjectKey:file-not-exist";

        // Check there are no measures
        assertEquals(2, context.measures(componentKey).size());
        assertEquals(0, context.measure(componentKey, CoreMetrics.NCLOC).value().intValue());
        assertEquals(0, context.measure(componentKey, CoreMetrics.COMMENT_LINES).value().intValue());
	}  
}