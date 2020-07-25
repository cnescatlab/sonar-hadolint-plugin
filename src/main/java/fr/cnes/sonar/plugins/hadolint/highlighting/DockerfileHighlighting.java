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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import fr.cnes.sonar.plugins.hadolint.settings.HadolintPluginProperties;

 /**
  * Class used to create and save highlighting for Dockerfiles.
  */
public class DockerfileHighlighting {
    private static final Logger LOGGER = Loggers.get(DockerfileHighlighting.class);

    private final SensorContext context;
    private final List<InputFile> inputFiles;

    /**
     * Constructor.
     * @param context Provides SonarQube services to register highlightings.
     * @param inputFiles The Dockerfiles to highlight.
     */
    public DockerfileHighlighting(final SensorContext context, final List<InputFile> inputFiles) {
        this.context = context;
        this.inputFiles = inputFiles;
    }

    /**
     * Analyses the provided Dockerfiles to highlight Keywords, Comments and Strings.
     */
    public void highlight() {
        BufferedReader reader;
        String line;
        int lineNumber;

        NewHighlighting newHighlighting;

        for (final InputFile dockerfile : inputFiles) {
            try {
                reader = new BufferedReader(new StringReader(dockerfile.contents()));
                lineNumber = 0;
                
                newHighlighting = context.newHighlighting();
                newHighlighting.onFile(dockerfile);
    
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    highlightLine(newHighlighting, line, lineNumber);
                }

                newHighlighting.save();

            } catch (final IOException e) {
                LOGGER.warn("Unable to count lines of comment for file " + dockerfile.filename() + ", ignoring measure", e);
            }
        }
    }

    /**
     * Hightlights Dockerfile specific keywords, comments & strings.
     * @param newHighlighting Provides SonarQube services to register a new highlighting.
     * @param line The line to analyse for possible highlighting.
     * @param lineNumber The current line number inside the analyzed file.
     */
    private void highlightLine(NewHighlighting newHighlighting, String line, int lineNumber) {
        // SIf the line is not empty
        if (line.length() > 0) {
            // Retrieve the first word of the line.
            String word = line.split(" ")[0];
            // Variable used to know where the comment mark is in a string
            int indexOfComment = -1;
            // Split the line using a " not preceded by a \ to easily identified string content
            String[] strings = line.split("(?<!\\\\)\"");
            // Variables used to determine the position of a string inside the line
            int indexOfString = -1;
            int nextIndexOfString = -1;

            // If the first word of the first part is a known Dockerfile Keyword, it is highlighted.
            if (HadolintPluginProperties.getDockerfileKeywords().contains(word)) {              
                newHighlighting.highlight(lineNumber, 0, lineNumber, word.length(), TypeOfText.KEYWORD);
            }

            // For each piece of line between an unescaped quote
            for(int i = 0; i < strings.length; i++) {   
                // Retrieve the first occurence of the comment symbol in the substring.
                indexOfComment = strings[i].indexOf('#');
                
                // If the index is even then it is not a string
                if (i%2 != 1 && indexOfComment > -1) {
                    // If the comment symbol is detected, we highlight all the line from the symbol position until the end as comment.
                    indexOfComment = line.indexOf('#');
                    newHighlighting.highlight(lineNumber, indexOfComment, lineNumber, line.length(), TypeOfText.COMMENT);
                    // All the following is a comment, so stop exploring the line
                    break;
                } else if (i%2 == 1) {
                    // If the index is odd then it is a string
                    // so we get the position of the string inside the line
                    // -1 to get the beginning quote
                    indexOfString = line.indexOf(strings[i]) - 1;
                    // +1 to get the ending quote
                    nextIndexOfString = indexOfString + strings[i].length() + 1;
                    // If the string is not properly ended before the end of the line
                    if (nextIndexOfString >= line.length()) {
                        nextIndexOfString = line.length() - 1;
                    }
                    newHighlighting.highlight(lineNumber, indexOfString, lineNumber, nextIndexOfString + 1, TypeOfText.STRING);
                }
            }
        }
    }
}