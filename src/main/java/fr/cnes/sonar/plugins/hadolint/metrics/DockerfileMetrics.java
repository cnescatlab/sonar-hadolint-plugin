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

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;

/**
 * Class used to create and save measures for Dockerfiles.
 */
public final class DockerfileMetrics {

    // Hide constructor
    private DockerfileMetrics() {}

    /**
     * Compute and save measures for linesOfCode & linesOfComment
     * @param context The SonarQube context in which we create measures
     * @param file The file on which we compute measures
     * @param tokenList The list of tokens found in file
     * @param fileLinesContextFactory SonarQube utility to add context on file lines
     */
    public static void saveLineTypes(SensorContext context, InputFile file, List<DockerfileToken> tokenList, FileLinesContextFactory fileLinesContextFactory) {
        // collect line types
        Set<Integer> linesOfCode = new HashSet<>();
        Set<Integer> linesOfComment = new HashSet<>();

        for (DockerfileToken token : tokenList) {
            for (int line = token.getStartLine(); line <= token.getEndLine(); line++) {
                if (token.getType().equals(DockerfileTokenType.COMMENT)) {
                    linesOfComment.add(line);
                } else {
                    linesOfCode.add(line);
                }
            }
        }

        context.<Integer>newMeasure().on(file).forMetric(CoreMetrics.NCLOC).withValue(linesOfCode.size()).save();
        context.<Integer>newMeasure().on(file).forMetric(CoreMetrics.COMMENT_LINES).withValue(linesOfComment.size()).save();

        FileLinesContext fileLinesContext = fileLinesContextFactory.createFor(file);
        linesOfCode.forEach(line -> fileLinesContext.setIntValue(CoreMetrics.NCLOC_DATA_KEY, line, 1));
        fileLinesContext.save();
    }

}