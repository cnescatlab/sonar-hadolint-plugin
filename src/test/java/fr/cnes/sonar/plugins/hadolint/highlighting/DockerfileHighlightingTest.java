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

package fr.cnes.sonar.plugins.hadolint.highlighting;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;

@RunWith(MockitoJUnitRunner.class)
public class DockerfileHighlightingTest {
    @Captor
    ArgumentCaptor<Integer> startLineCaptor;
    @Captor
    ArgumentCaptor<Integer> startLineOffsetCaptor;
    @Captor
    ArgumentCaptor<Integer> endLineCaptor;
    @Captor
    ArgumentCaptor<Integer> endLineOffsetCaptor;
    @Captor
    ArgumentCaptor<TypeOfText> typeOfTextCaptor;

    @Test
    public void testMetricsAreAllGood() throws IOException {
        
        // Set up InputFile to create highlightings on
        String testFileDir = DockerfileHighlightingTest.class.getClassLoader().getResource("project").getFile();
        String testFilePath = testFileDir + "/Dockerfile_highlight";
        DefaultInputFile testFile = TestInputFileBuilder
            .create("ProjectKey", new File(testFileDir), new File(testFilePath))
            .setLanguage("Dockerfile")
            .setContents(new String(Files.readAllBytes(Paths.get(testFilePath))))
            .initMetadata("public class Foo {\n}").build();
        
        List<InputFile> inputFiles = new ArrayList<>();
        inputFiles.add(testFile);       

        // Set up highlighting mock
        SensorContext context = Mockito.mock(SensorContext.class);
        NewHighlighting newHighlighting = Mockito.mock(NewHighlighting.class);
        Mockito.when(context.newHighlighting()).thenReturn(newHighlighting);

        // Generate highlightings
        DockerfileHighlighting highlighting = new DockerfileHighlighting(context, inputFiles);
        highlighting.highlight();

        // Capture calls to highlight, then check method is called with right parameters
        Mockito.verify(newHighlighting, times(9)).highlight(
            startLineCaptor.capture(), 
            startLineOffsetCaptor.capture(), 
            endLineCaptor.capture(), 
            endLineOffsetCaptor.capture(), 
            typeOfTextCaptor.capture());
        
        //Verify startLine parameter values
        assertEquals(1, startLineCaptor.getAllValues().get(0).intValue());
        assertEquals(2, startLineCaptor.getAllValues().get(1).intValue());
        assertEquals(4, startLineCaptor.getAllValues().get(2).intValue());
        assertEquals(5, startLineCaptor.getAllValues().get(3).intValue());
        assertEquals(7, startLineCaptor.getAllValues().get(4).intValue());
        assertEquals(9, startLineCaptor.getAllValues().get(5).intValue());
        assertEquals(10, startLineCaptor.getAllValues().get(6).intValue());
        assertEquals(10, startLineCaptor.getAllValues().get(7).intValue());
        assertEquals(10, startLineCaptor.getAllValues().get(8).intValue());

        //Verify startLineOffset parameter values
        assertEquals(0, startLineOffsetCaptor.getAllValues().get(0).intValue());
        assertEquals(0, startLineOffsetCaptor.getAllValues().get(1).intValue());
        assertEquals(0, startLineOffsetCaptor.getAllValues().get(2).intValue());
        assertEquals(0, startLineOffsetCaptor.getAllValues().get(3).intValue());
        assertEquals(0, startLineOffsetCaptor.getAllValues().get(4).intValue());
        assertEquals(0, startLineOffsetCaptor.getAllValues().get(5).intValue());
        assertEquals(0, startLineOffsetCaptor.getAllValues().get(6).intValue());
        assertEquals(12, startLineOffsetCaptor.getAllValues().get(7).intValue());
        assertEquals(20, startLineOffsetCaptor.getAllValues().get(8).intValue());

        //Verify endLine parameter values
        assertEquals(1, endLineCaptor.getAllValues().get(0).intValue());
        assertEquals(2, endLineCaptor.getAllValues().get(1).intValue());
        assertEquals(4, endLineCaptor.getAllValues().get(2).intValue());
        assertEquals(5, endLineCaptor.getAllValues().get(3).intValue());
        assertEquals(7, endLineCaptor.getAllValues().get(4).intValue());
        assertEquals(9, endLineCaptor.getAllValues().get(5).intValue());
        assertEquals(10, endLineCaptor.getAllValues().get(6).intValue());
        assertEquals(10, endLineCaptor.getAllValues().get(7).intValue());
        assertEquals(10, endLineCaptor.getAllValues().get(8).intValue());

        //Verify endLineOffset parameter values
        assertEquals(22, endLineOffsetCaptor.getAllValues().get(0).intValue());
        assertEquals(4, endLineOffsetCaptor.getAllValues().get(1).intValue());
        assertEquals(7, endLineOffsetCaptor.getAllValues().get(2).intValue());
        assertEquals(7, endLineOffsetCaptor.getAllValues().get(3).intValue());
        assertEquals(3, endLineOffsetCaptor.getAllValues().get(4).intValue());
        assertEquals(56, endLineOffsetCaptor.getAllValues().get(5).intValue());
        assertEquals(10, endLineOffsetCaptor.getAllValues().get(6).intValue());
        assertEquals(18, endLineOffsetCaptor.getAllValues().get(7).intValue());
        assertEquals(24, endLineOffsetCaptor.getAllValues().get(8).intValue());

        //Verify typeOfText parameter values
        assertEquals(TypeOfText.COMMENT, typeOfTextCaptor.getAllValues().get(0));
        assertEquals(TypeOfText.KEYWORD, typeOfTextCaptor.getAllValues().get(1));
        assertEquals(TypeOfText.KEYWORD, typeOfTextCaptor.getAllValues().get(2));
        assertEquals(TypeOfText.KEYWORD, typeOfTextCaptor.getAllValues().get(3));
        assertEquals(TypeOfText.KEYWORD, typeOfTextCaptor.getAllValues().get(4));
        assertEquals(TypeOfText.COMMENT, typeOfTextCaptor.getAllValues().get(5));
        assertEquals(TypeOfText.KEYWORD, typeOfTextCaptor.getAllValues().get(6));
        assertEquals(TypeOfText.STRING, typeOfTextCaptor.getAllValues().get(7));
        assertEquals(TypeOfText.STRING, typeOfTextCaptor.getAllValues().get(8));
    }

    @Test
    public void testWhenFileIsNotFound() throws IOException {
        // Set up InputFile to create highlightings on
        String testFileDir = DockerfileHighlightingTest.class.getClassLoader().getResource("project").getFile();
        // The path does not exist
        String testFilePath = testFileDir + "/file-not-found";
        DefaultInputFile testFile = TestInputFileBuilder
            .create("ProjectKey", new File(testFileDir), new File(testFilePath))
            .setLanguage("Dockerfile")
            .initMetadata("public class Foo {\n}").build();
        
        List<InputFile> inputFiles = new ArrayList<>();
        inputFiles.add(testFile);       

        // Set up highlighting mock
        SensorContext context = Mockito.mock(SensorContext.class);
        NewHighlighting newHighlighting = Mockito.mock(NewHighlighting.class);

        // Generate highlightings
        DockerfileHighlighting highlighting = new DockerfileHighlighting(context, inputFiles);
        highlighting.highlight();

        // Capture calls to highlight, then check method is never called
        Mockito.verify(newHighlighting, never()).highlight(
            startLineCaptor.capture(), 
            startLineOffsetCaptor.capture(), 
            endLineCaptor.capture(), 
            endLineOffsetCaptor.capture(), 
            typeOfTextCaptor.capture());
    }    
    
}