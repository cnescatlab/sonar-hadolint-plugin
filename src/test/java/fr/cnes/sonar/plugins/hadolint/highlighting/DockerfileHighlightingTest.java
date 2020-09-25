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

import fr.cnes.sonar.plugins.hadolint.lexer.DockerfileToken;
import fr.cnes.sonar.plugins.hadolint.lexer.DockerfileTokenType;

import com.sonar.sslr.api.Token;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.net.URI;

import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;

@RunWith(MockitoJUnitRunner.class)
public class DockerfileHighlightingTest {

    private SensorContextTester sensorContext;
    private DefaultInputFile testFile;

    /** 
     * Method used to build specific DockerfileToken
     * @param uri Uri of the file on which to create a token
     * @param type Type of Token to create
     * @param line Line of file on which we create a token
     * @param column Column of line on which we create a token
     * @param value String value of the token
     * @return DockerfileToken created with desired attributes
    */
    private DockerfileToken createToken(URI uri, DockerfileTokenType type, int line, int column, String value) {
        Token tmpToken = Token.builder()
          .setURI(uri)
          .setType(type)
          .setLine(line)
          .setColumn(column)
          .setValueAndOriginalValue(value)
          .build();

        return new DockerfileToken(tmpToken);
    }

    /**
     * Method used to ease asserts on highlightings
     * @param line Line of file where to seek for a token
     * @param column Column in line where to seek for a token
     * @param length Length of value of the token to seek
     * @param type Type of token to seek
     */
    private void assertHighlighting(int line, int column, int length, TypeOfText type) {
        for (int i = column; i < column + length; i++) {
            List<TypeOfText> typeOfTexts = sensorContext.highlightingTypeAt(testFile.key(), line, i);
            assertThat(typeOfTexts).containsOnly(type);
        }
    }

    @Test
    public void testHighlightingsAreAllGood() throws IOException {
        
        // Set up InputFile to create highlightings on
        String testFileDir = DockerfileHighlightingTest.class.getClassLoader().getResource("project").getFile();
        String testFilePath = testFileDir + "/Dockerfile_highlight";
        testFile = TestInputFileBuilder
            .create("ProjectKey", new File(testFileDir), new File(testFilePath))
            .setLanguage("Dockerfile")
            .setContents(new String(Files.readAllBytes(Paths.get(testFilePath))))
            .build();

        // Set up context
        sensorContext = SensorContextTester.create(new File(testFileDir));
        sensorContext.fileSystem().add(testFile);

        // Generate a list of token
        List<DockerfileToken> tokenList = new ArrayList();
        URI testFileUri = testFile.path().toUri();
        tokenList.add(createToken(testFileUri, DockerfileTokenType.COMMENT, 1, 0, "# from base image node"));
        tokenList.add(createToken(testFileUri, DockerfileTokenType.KEYWORD, 2, 0, "FROM"));
        tokenList.add(createToken(testFileUri, DockerfileTokenType.KEYWORD, 4, 0, "WORKDIR"));
        tokenList.add(createToken(testFileUri, DockerfileTokenType.KEYWORD, 5, 0, "WORKDIR"));
        tokenList.add(createToken(testFileUri, DockerfileTokenType.KEYWORD, 7, 0, "RUN"));
        tokenList.add(createToken(testFileUri, DockerfileTokenType.COMMENT, 9, 0, "# command executable and version"));
        tokenList.add(createToken(testFileUri, DockerfileTokenType.KEYWORD, 10, 0, "ENTRYPOINT"));
        tokenList.add(createToken(testFileUri, DockerfileTokenType.STRING, 10, 12, "\"node\""));

        // Generate highlightings
        DockerfileHighlighting.saveHighlight(sensorContext, testFile, tokenList);
        
        // Check highlighting is correct
        assertHighlighting(1, 0, 22, TypeOfText.COMMENT);
        assertHighlighting(2, 0, 4, TypeOfText.KEYWORD);
        assertHighlighting(4, 0, 7, TypeOfText.KEYWORD);
        assertHighlighting(5, 0, 7, TypeOfText.KEYWORD);
        assertHighlighting(7, 0, 3, TypeOfText.KEYWORD);
        assertHighlighting(9, 0, 32, TypeOfText.COMMENT);
        assertHighlighting(10, 0, 10, TypeOfText.KEYWORD);
        assertHighlighting(10, 12, 6, TypeOfText.STRING);
    }
}