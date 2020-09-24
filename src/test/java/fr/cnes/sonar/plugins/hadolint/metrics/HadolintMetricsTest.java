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

import fr.cnes.sonar.plugins.hadolint.lexer.DockerfileToken;
import fr.cnes.sonar.plugins.hadolint.lexer.DockerfileTokenType;

import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import com.sonar.sslr.api.Token;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.net.URI;

import org.junit.Test;
import org.mockito.Mockito;

public class HadolintMetricsTest {

    /** 
     * 
    */
    private DockerfileToken createToken(URI uri, DockerfileTokenType type, int line, String value) {
        Token tmpToken = Token.builder()
          .setURI(uri)
          .setType(type)
          .setLine(line)
          .setColumn(0)
          .setValueAndOriginalValue(value)
          .build();

        return new DockerfileToken(tmpToken);
    }

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

        // Generate a list of token
        List<DockerfileToken> tokenList = new ArrayList();
        URI testFileUri = testFile.path().toUri();
        tokenList.add(createToken(testFileUri, DockerfileTokenType.COMMENT, 1, ""));
        tokenList.add(createToken(testFileUri, DockerfileTokenType.COMMENT, 9, ""));
        tokenList.add(createToken(testFileUri, DockerfileTokenType.KEYWORD, 2, ""));
        tokenList.add(createToken(testFileUri, DockerfileTokenType.KEYWORD, 4, ""));
        tokenList.add(createToken(testFileUri, DockerfileTokenType.KEYWORD, 5, ""));
        tokenList.add(createToken(testFileUri, DockerfileTokenType.KEYWORD, 7, ""));
        tokenList.add(createToken(testFileUri, DockerfileTokenType.KEYWORD, 10, ""));

        // Generates fake linesContetFactory
        FileLinesContext linesContext = Mockito.mock(FileLinesContext.class);
        FileLinesContextFactory linesContextFactory = Mockito.mock(FileLinesContextFactory.class);
        Mockito.when(linesContextFactory.createFor(testFile)).thenReturn(linesContext);
        
        // Generate measures
        DockerfileMetrics.saveLineTypes(context, testFile, tokenList, linesContextFactory);
        String componentKey = "ProjectKey:Dockerfile";

        // Check the measures are OK
        assertEquals(2, context.measures(componentKey).size());
        assertEquals(5, context.measure(componentKey, CoreMetrics.NCLOC).value().intValue());
        assertEquals(2, context.measure(componentKey, CoreMetrics.COMMENT_LINES).value().intValue());
    }
}