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
package fr.cnes.sonar.plugins.hadolint.settings;

import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import java.util.Arrays;
import java.util.List;

/**
 * Define all SonarQube properties provided by this plugin.
 */
public class HadolintPluginProperties {

    private HadolintPluginProperties() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Prefix used by all properties of this plugin.
     **/
    private static final String PROPERTIES_PREFIX = "sonar.hadolint.";

    /**
     * Hadolint name.
     **/
    public static final String HADOLINT_NAME = "Hadolint";

    /**
     * List of know keywords in Dockerfiles
     */
    private static final List<String> DOCKERFILE_KEYWORDS = Arrays.asList("FROM", "RUN", "CMD", "LABEL", "MAINTAINER",
            "EXPOSE", "ENV", "ADD", "COPY", "ENTRYPOINT", "VOLUME", "USER", "WORKDIR", "ARG", "ONBUILD", "STOPSIGNAL",
            "HEALTHCHECK", "SHELL");

    /**
     * Path to Hadolint rules definition folder
     */
    public static final String HADOLINT_RULES_DEFINITION_FOLDER = "rules/hadolint";

    /**
     * Path to Shellcheck rules definition folder
     */
    public static final String SHELLCHECK_RULES_DEFINITION_FOLDER = "rules/shellcheck";

    // Whether to use SonarQube Hadolint Plugin
    /**
     * Key for the activated property
     **/
    public static final String ACTIVATED_KEY = PROPERTIES_PREFIX + "activated";
    /**
     * Name for the report path property
     **/
    public static final String ACTIVATED_NAME = "Hadolint Plugin Activation";
    /**
     * Description for the report path property
     **/
    public static final String ACTIVATED_DESC = "Whether you need to activate Hadolint Plugin for this analysis.";
    /**
     * Default value for the report path property
     **/
    public static final String ACTIVATED_DEFAULT = "false";

    // Reports path
    /**
     * Key for the report path property
     **/
    public static final String REPORT_PATH_KEY = PROPERTIES_PREFIX + "reports.path";
    /**
     * Name for the report path property
     **/
    public static final String REPORT_PATH_NAME = "Report files";
    /**
     * Description for the report path property
     **/
    public static final String REPORT_PATH_DESC = "Path to the Hadolint reports in ChekStyle format. Multiple path can be provided.";
    /**
     * Default value for the report path property
     **/
    public static final String REPORT_PATH_DEFAULT = "hadolint-report.xml";

    // Analyzed files path
    /**
     * Key for the analyzed files path property
     **/
    public static final String DOCKERFILES_PATH_KEY = PROPERTIES_PREFIX + "dockerfiles.path";
    /**
     * Name for the analyzed files path property
     **/
    public static final String DOCKERFILES_PATH_NAME = "Dockerfiles path";
    /**
     * Description for the analyzed files path property
     **/
    public static final String DOCKERFILES_PATH_DESC = "Path to the analyzed Dockerfiles. Multiple path can be provided.";
    /**
     * Default value for the analyzed files path property
     **/
    public static final String DOCKERFILES_PATH_DEFAULT = "Dockerfile";

    /**
     * Plugin properties extensions.
     *
     * @return The list of built properties.
     */
    public static List<PropertyDefinition> getProperties() {
        return Arrays.asList(
            PropertyDefinition.builder(ACTIVATED_KEY)
                    .defaultValue(ACTIVATED_DEFAULT)
                    .category(HADOLINT_NAME)
                    .name(ACTIVATED_NAME)
                    .description(ACTIVATED_DESC)
                    .type(PropertyType.BOOLEAN)
                    .onQualifiers(Qualifiers.PROJECT)
                    .build()
            ,
            PropertyDefinition.builder(DOCKERFILES_PATH_KEY).multiValues(true)
                    .defaultValue(DOCKERFILES_PATH_DEFAULT)
                    .category(HADOLINT_NAME)
                    .name(DOCKERFILES_PATH_NAME)
                    .description(DOCKERFILES_PATH_DESC)
                    .onQualifiers(Qualifiers.PROJECT)
                    .build()
            ,
            PropertyDefinition.builder(REPORT_PATH_KEY).multiValues(true)
                    .defaultValue(REPORT_PATH_DEFAULT)
                    .category(HADOLINT_NAME)
                    .name(REPORT_PATH_NAME)
                    .description(REPORT_PATH_DESC)
                    .onQualifiers(Qualifiers.PROJECT)
                    .build()
        );
    }

    public static List<String> getDockerfileKeywords() {
        return DOCKERFILE_KEYWORDS;
    }

}
