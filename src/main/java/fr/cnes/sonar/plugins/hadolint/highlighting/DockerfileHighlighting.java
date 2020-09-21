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

import java.util.List;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;

import fr.cnes.sonar.plugins.hadolint.lexer.DockerfileToken;

 /**
  * Class used to create and save highlighting for Dockerfiles.
  */
public final class DockerfileHighlighting {

    // Hide constructor
    private DockerfileHighlighting() {}

    /**
     * Hightlights Dockerfile specific keywords, comments & strings.
     * @param context The SonarQube context in which we create highlightings
     * @param file The file on which we create highlightings
     * @param tokenList The list of tokens found in file
     */
    public static void saveHighlight(SensorContext context, InputFile file, List<DockerfileToken> tokenList) {
        NewHighlighting highlighting = context.newHighlighting().onFile(file);

        for (int i = 0; i < tokenList.size(); i++) {
            DockerfileToken currentToken = tokenList.get(i);
            TypeOfText highlightingType = null;

            switch (currentToken.getType()) {
                case COMMENT:
                    highlightingType = TypeOfText.COMMENT;
                    break;

                case STRING:
                    highlightingType = TypeOfText.STRING;
                    break;

                case KEYWORD:
                    highlightingType = TypeOfText.KEYWORD;
                    break;

                default:
                    highlightingType = null;
            }

            if (highlightingType != null) {
                highlighting.highlight(currentToken.getStartLine(), currentToken.getStartColumn(), currentToken.getEndLine(), currentToken.getEndColumn(), highlightingType);
            }
        }

        highlighting.save();
    }

}