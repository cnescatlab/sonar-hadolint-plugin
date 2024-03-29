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

import fr.cnes.sonar.plugins.hadolint.languages.*;
import fr.cnes.sonar.plugins.hadolint.model.CheckstyleError;
import fr.cnes.sonar.plugins.hadolint.model.CheckstyleFile;
import fr.cnes.sonar.plugins.hadolint.model.CheckstyleReport;
import fr.cnes.sonar.plugins.hadolint.rules.HadolintRulesDefinition;
import fr.cnes.sonar.plugins.hadolint.settings.HadolintPluginProperties;
import fr.cnes.sonar.plugins.hadolint.model.XmlHandler;
import fr.cnes.sonar.plugins.hadolint.utils.DirectoryScanner;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.config.Configuration;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.api.utils.WildcardPattern;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Executed during sonar-scanner call. Import checkstyle formatted Hadolint
 * reports into SonarQube.
 */
public class HadolintSensor implements Sensor {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Loggers.get(HadolintSensor.class);

    /**
     * Give information about this sensor.
     *
     * @param sensorDescriptor Descriptor injected to set the sensor.
     */
    @Override
    public void describe(SensorDescriptor sensorDescriptor) {
        // Defines sensor name
        sensorDescriptor.name(getClass().getName());

        // Only main files are concerned, not tests.
        sensorDescriptor.onlyOnFileType(InputFile.Type.MAIN);

        // This sensor is activated only if a rule from the following repo is activated.
        sensorDescriptor.createIssuesForRuleRepositories(
                HadolintRulesDefinition.getRepositoryKeyForLanguage(DockerfileLanguage.KEY));

        // Prevents sensor to be run when no Dockerfiles found.
        sensorDescriptor.onlyOnLanguages(DockerfileLanguage.KEY);
    }

    /**
     * Execute the analysis.
     *
     * @param sensorContext Provide SonarQube services to register results.
     */
    @Override
    public void execute(SensorContext sensorContext) {

        // Represent the configuration used for the analysis.
        final Configuration config = sensorContext.config();

        // Represent the file system used for the analysis.
        final FileSystem fileSystem = sensorContext.fileSystem();
        // Represent the active rules used for the analysis.
        final ActiveRules activeRules = sensorContext.activeRules();

        // Report files found in file system and corresponding to SQ property.
        final List<String> reportFiles = getReportFiles(config, fileSystem);

        // If exists, unmarshal each xml result file.
        for (final String reportPath : reportFiles) {
            try {
                // Unmarshall the xml.
                final FileInputStream file = new FileInputStream(fileSystem.resolvePath(reportPath));
                final CheckstyleReport checkstyleReport = (CheckstyleReport) XmlHandler.unmarshal(file,
                        CheckstyleReport.class);
                // Retrieve file in a SonarQube format.
                final Map<String, InputFile> scannedFiles = getScannedFiles(fileSystem, checkstyleReport);

                // Handles issues.
                for (final CheckstyleFile checkstyleFile : checkstyleReport.getCheckstyleFiles()) {
                    for (final CheckstyleError checkstyleError : checkstyleFile.getChecktyleErrors()) {

                        if (isRuleActive(activeRules, checkstyleError.getSource())) { // manage active rules
                            saveIssue(sensorContext, scannedFiles, checkstyleError, checkstyleFile);
                        } else { // log ignored data
                            LOGGER.info(String.format(
                                    "An issue for rule '%s' was detected by Hadolint but this rule is deactivated in current analysis.",
                                    checkstyleError.getSource()));
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                LOGGER.error(e.getMessage(), e);
                sensorContext.newAnalysisError().message(e.getMessage()).save();
            }
        }

    }

    /**
     * This method saves an issue into the SonarQube service.
     *
     * @param context A SensorContext to reach SonarQube services.
     * @param files   Map containing files in SQ format.
     * @param issue   A AnalysisRule with the convenient format for Hadolint.
     */
    static void saveIssue(final SensorContext context, final Map<String, InputFile> files, final CheckstyleError issue,
            final CheckstyleFile file) {

        // Retrieve the file containing the issue.
        final InputFile inputFile = files.getOrDefault(file.getName(), null);

        if (inputFile != null) {
            // Retrieve the ruleKey if it exists.
            final RuleKey ruleKey = RuleKey
                    .of(HadolintRulesDefinition.getRepositoryKeyForLanguage(DockerfileLanguage.KEY), issue.getSource());

            // Create a new issue for SonarQube, but it must be saved using NewIssue.save().
            final NewIssue newIssue = context.newIssue();
            // Create a new location for this issue.
            final NewIssueLocation newIssueLocation = newIssue.newLocation();

            // Set trivial issue's attributes from AnalysisRule fields.
            newIssueLocation.on(inputFile);
            newIssueLocation.at(inputFile.selectLine(Integer.parseInt(issue.getLine())));
            newIssueLocation.message(issue.getMessage());
            newIssue.forRule(ruleKey);
            newIssue.at(newIssueLocation);
            newIssue.save();
        } else {
            LOGGER.error(String.format("Issue '%s' on file '%s' has not been saved because source file was not found.",
                    issue.getSource(), file.getName()));
        }

    }

    /**
     * Construct a map with all source files mentionned in Hadolint report.
     *
     * @param fileSystem       The file system on which the analysis is running.
     * @param checkstyleReport The checkstyle report
     * @return A possibly empty Map of InputFile.
     */
    private Map<String, InputFile> getScannedFiles(final FileSystem fileSystem,
            final CheckstyleReport checkstyleReport) {
        // Contains the result to be returned.
        final Map<String, InputFile> result = new HashMap<>();
        final List<CheckstyleFile> files = checkstyleReport.getCheckstyleFiles();
        // Looks for each file in file system, print an error if not found.
        for (final CheckstyleFile file : files) {
            // Checks if the file system contains a file with corresponding path (relative
            // or absolute).
            FilePredicate predicate = fileSystem.predicates().hasPath(file.getName());
            InputFile inputFile = fileSystem.inputFile(predicate);
            if (inputFile != null) {
                result.put(file.getName(), inputFile);
            } else {
                LOGGER.error(String.format("The source file '%s' mentionned in Hadolint report was not found.",
                        file.getName()));
            }
        }

        return result;
    }

    /**
     * Returns a list of processable result file's path.
     *
     * @param config     Configuration of the analysis where properties are put.
     * @param fileSystem The current file system.
     * @return Return a list of path 'findable' in the file system.
     */
    private List<String> getReportFiles(final Configuration config, final FileSystem fileSystem) {
        // List of found reports
        List<String> result = new ArrayList<>();

        // Retrieves the non-verified path list from the SonarQube property.
        String reportPathPropertyKey = HadolintPluginProperties.REPORT_PATH_KEY;
        final String reportPaths = config.get(reportPathPropertyKey)
                .orElse(HadolintPluginProperties.REPORT_PATH_DEFAULT);

        // Check if each path is known by the file system and add it to the processable
        // path list, otherwise print a warning and ignore this report file.
        DirectoryScanner scanner = null;
        List<String> includedFiles = null;
        String baseDirPath = fileSystem.baseDir().getPath();

        for (String reportPath : reportPaths.split(",")) {
            scanner = new DirectoryScanner(new File(baseDirPath), WildcardPattern.create(reportPath));
            includedFiles = scanner.getIncludedFiles();

            if (includedFiles.isEmpty()) {
                if (config.hasKey(reportPathPropertyKey)) {
                    // try absolute path
                    File file = new File(reportPath);
                    if (!file.exists() || !file.isFile()) {
                        LOGGER.warn("No report was found using pattern {}", reportPath);
                    } else {
                        result.add(file.getPath());
                        LOGGER.info("Report {} was found and will be processed", file.getPath());
                    }
                } else {
                    LOGGER.debug("No report was found using default pattern");
                }
            } else {
                result.addAll(includedFiles);
                LOGGER.info("Reports were found ({}) using pattern {} and will be processed", includedFiles.size(),
                        reportPath);
            }
        }

        return result;
    }

    /**
     * Check if a rule is activated in current analysis.
     *
     * @param activeRules Set of active rules during an analysis.
     * @param rule        Key (Hadolint) of the rule to check.
     * @return True if the rule is active and false if not or not exists.
     */
    private boolean isRuleActive(final ActiveRules activeRules, final String rule) {
        final RuleKey ruleKeyDockerfile = RuleKey
                .of(HadolintRulesDefinition.getRepositoryKeyForLanguage(DockerfileLanguage.KEY), rule);
        return activeRules.find(ruleKeyDockerfile) != null;
    }
}
