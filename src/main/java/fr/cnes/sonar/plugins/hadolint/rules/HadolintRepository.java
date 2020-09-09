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
package fr.cnes.sonar.plugins.hadolint.rules;

import fr.cnes.sonar.plugins.hadolint.rules.FileSystem;
import fr.cnes.sonar.plugins.hadolint.languages.DockerfileLanguage;
import fr.cnes.sonar.plugins.hadolint.settings.HadolintPluginProperties;

import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class used to collect JSON/HTML rule files
 * This class is used to create rules & quality profile
 */
public class HadolintRepository {

    private static final Logger LOGGER = Loggers.get(HadolintRepository.class);
    private static final List<String> RULE_KEYS = new ArrayList<>();

    // Analyze the resources to find the rule files
    static {
        extractRules(HadolintPluginProperties.HADOLINT_RULES_DEFINITION_FOLDER, "Hadolint");
        extractRules(HadolintPluginProperties.SHELLCHECK_RULES_DEFINITION_FOLDER, "ShellCheck");
    }

    /**
     * Hide constructor
     */
    private HadolintRepository() {
    }

    /**
     * Method used to extract rule files for a specific folder
     */
    private static void extractRules(String folder, String ruleType) {
        // Find the directory which contains the files
        URL definitionDir = HadolintRepository.class.getClassLoader().getResource(folder);
        
        // Warn if directory does not exists
        if (definitionDir == null) {
            LOGGER.info("No {} rules found", ruleType);
        } else {
            LOGGER.debug("Creating FileSystem for " + definitionDir);

            try (FileSystem fs = new FileSystem(definitionDir.toURI())) {
                LOGGER.debug("Reading rule definition files...");

                // Find every json rule definition file in the directory
                fs.readDirectory(definitionDir.toURI()).filter(entry -> entry.toString().endsWith(".json")).forEach(entry -> {
                    String key = getRuleKey(entry);
                    LOGGER.debug("RuleKey of {} is {}", entry.toString(), key);
                    // If an HTML file with identical filename exists, then it's OK to add the ruel
                    if (htmlDescFileExists(entry)) {
                        RULE_KEYS.add(key);
                    } else {
                        LOGGER.warn("Rule {} defined but not described (.html file missing)", key);
                    }
                });
            } catch (URISyntaxException e) {
                LOGGER.error("Cannot find {} rules", ruleType, e);
            } catch (IOException e) {
                LOGGER.error("Unknown error", e);
            }
        }
    }

    /**
     * Returns the {@code RuleKey} of the rule identified by its Id as a string
     *
     * @param ruleKey a rule key passed as a string
     * @return a {@code RuleKey} if found or {@code null}
     */
    public static RuleKey getRuleKey(String ruleKey) {
        return RuleKey.of(DockerfileLanguage.KEY, ruleKey);
    }

    /**
     * Returns the keys of all Hadolint rules supported by this plugin
     *
     * @return the keys of all Hadolint rules supported by this plugin
     */
    public static List<String> getHadolintRuleKeys() {
        return RULE_KEYS.stream().filter(key -> key.startsWith("DL")).collect(Collectors.toList());
    }

    /**
     * Returns the keys of all ShellCheck rules supported by this plugin
     *
     * @return the keys of all ShellCheck rules supported by this plugin
     */
    public static List<String> getShellCheckRuleKeys() {
        return RULE_KEYS.stream().filter(key -> key.startsWith("SC")).collect(Collectors.toList());
    }

    /**
     * Returns the rule key from filename
     */
    private static String getRuleKey(Path definitionFile) {
        return definitionFile.getFileName().toString().replace(".json", "");
    }

    /**
     * Checks if a html description file exists
     */
    private static boolean htmlDescFileExists(Path definitionFile) {
        return Files.exists(definitionFile.resolveSibling(getRuleKey(definitionFile) + ".html"));
    }
    
}
