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

import fr.cnes.sonar.plugins.hadolint.highlighting.DockerfileHighlighting;
import fr.cnes.sonar.plugins.hadolint.languages.DockerfileLanguage;
import fr.cnes.sonar.plugins.hadolint.lexer.DockerfileToken;
import fr.cnes.sonar.plugins.hadolint.lexer.Tokenizer;
import fr.cnes.sonar.plugins.hadolint.metrics.DockerfileMetrics;

import java.io.IOException;
import java.util.List;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

/**
 * 
 */
public class MetricSensor implements Sensor {
    
    private static final Logger LOG = Loggers.get(MetricSensor.class);

    private final FileLinesContextFactory fileLinesContextFactory;

    public MetricSensor(FileLinesContextFactory fileLinesContextFactory) {
        this.fileLinesContextFactory = fileLinesContextFactory;
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor
        .name(getClass().getName())
        .onlyOnLanguage(DockerfileLanguage.KEY);
    }

    @Override
    public void execute(SensorContext context) {
        FileSystem fileSystem = context.fileSystem();
        Iterable<InputFile> inputFiles = fileSystem.inputFiles(fileSystem.predicates().hasLanguage(DockerfileLanguage.KEY));

        Tokenizer tokenizer = new Tokenizer();

        for (InputFile file : inputFiles) {
            try {
                List<DockerfileToken> tokenList = tokenizer.tokenize(file.contents());

                DockerfileHighlighting.saveHighlight(context, file, tokenList);
                DockerfileMetrics.saveLineTypes(context, file, tokenList, this.fileLinesContextFactory);

            } catch (IOException e) {
                LOG.error(String.format("Failed to read file '%s'", file.toString()), e);
            }
        }
    }

}
