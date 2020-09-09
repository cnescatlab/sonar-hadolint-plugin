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
package fr.cnes.sonar.plugins.hadolint.languages;

import java.io.InputStream;

import fr.cnes.sonar.plugins.hadolint.rules.HadolintRepository;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

import fr.cnes.sonar.plugins.hadolint.model.Rule;
import fr.cnes.sonar.plugins.hadolint.model.RulesDefinition;
import fr.cnes.sonar.plugins.hadolint.model.XmlHandler;
import fr.cnes.sonar.plugins.hadolint.rules.HadolintRulesDefinition;
import fr.cnes.sonar.plugins.hadolint.settings.HadolintPluginProperties;

/**
 * Built-in quality profile format since SonarQube 6.6.
 */
public final class HadolintQualityProfiles implements BuiltInQualityProfilesDefinition {

    /** Display name for the built-in quality profile. **/
    private static final String HADOLINT_RULES_PROFILE_NAME = "Sonar way";

    /**
     * Allow to create a plugin.
     *
     * @param context Context of the plugin.
     */
    @Override
    public void define(Context context) {
        createBuiltInProfile(context, DockerfileLanguage.KEY, HadolintPluginProperties.HADOLINT_RULES_DEFINITION_FOLDER);
    }

    /**
     * Create a built in quality profile for a specific language.
     *
     * @param context SonarQube context in which create the profile.
     * @param language Language key of the associated profile.
     * @param path Path to the xml definition of all rules.
     */
    private void createBuiltInProfile(final Context context, final String language, final String path) {
        // Create a builder for the rules' repository.
        final NewBuiltInQualityProfile defaultProfile =
                context.createBuiltInQualityProfile(HADOLINT_RULES_PROFILE_NAME, language);

        // Retrieve all defined rules.
        HadolintRepository.getHadolintRuleKeys().forEach(key -> defaultProfile.activateRule(language, key));
        HadolintRepository.getShellCheckRuleKeys().forEach(key -> defaultProfile.activateRule(language, key));

        // Save the default profile.
        defaultProfile.setDefault(true);
        defaultProfile.done();
    }
}