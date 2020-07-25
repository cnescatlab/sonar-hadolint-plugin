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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.measure.Metric;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

/**
 * Class used to create and save measures for Dockerfiles.
 */
public class DockerfileMetrics {
    private static final Logger LOGGER = Loggers.get(DockerfileMetrics.class);

    private final SensorContext context;
    private final List<InputFile> inputFiles;

    /**
     * Constructor
     * @param context Provides SonarQube services to register results.
     * @param inputFiles The Dockerfiles to measure.
     */
    public DockerfileMetrics(final SensorContext context, final List<InputFile> inputFiles) {
        this.context = context;
        this.inputFiles = inputFiles;
    }

    /**
     * Analyses the provided Dockerfiles to measure their number of lines of code and comments.
     */
    public void analyse() {
        int lineCodeNumber = 0;
        int lineCommentNumber = 0;
        String line;
        BufferedReader reader;

        for (final InputFile dockerfile : inputFiles) {

            try {
                reader = new BufferedReader(new StringReader(dockerfile.contents()));
                while ((line = reader.readLine()) != null) {
                    if (!isCommentLine(line) && !isBlankLine(line)) {
                        lineCodeNumber++;
                    } else if (isCommentLine(line)){
                        lineCommentNumber++;
                    }
                }                
            } catch (final IOException e) {
                LOGGER.warn("Unable to count lines of code & comments for file " + dockerfile.filename() + ", ignoring measures", e);
            }

            saveMetricOnFile(dockerfile, CoreMetrics.NCLOC, lineCodeNumber);
            saveMetricOnFile(dockerfile, CoreMetrics.COMMENT_LINES, lineCommentNumber);
            lineCodeNumber = 0;
            lineCommentNumber = 0;
        }
    }

    /**
     * Indicates whether a line is a comment or not.
     * @param line The line to study.
     * @return True if the line is a comment, False otherwise.
     */
    private boolean isCommentLine(final String line) {
        return line.trim().matches("^[ \\t]*#[ \\t]*\\S.*");
    }

    /**
     * Indicates whether a line is empty or not.
     * @param line The line to study.
     * @return True if the line is empty, False otherwise.
     */
    private boolean isBlankLine(final String line) {
        return StringUtils.isBlank(line);
    }

    /**
     * Defines and saves a measure using SonarQube services.
     * @param inputFile The file with a new metric to save.
     * @param metric The metric we want to add.
     * @param value The value of the specified metric.
     */
    private void saveMetricOnFile(final InputFile inputFile, final Metric<Integer> metric, final Integer value) {
        context.<Integer>newMeasure()
          .withValue(value)
          .forMetric(metric)
          .on(inputFile)
          .save();
      }

}