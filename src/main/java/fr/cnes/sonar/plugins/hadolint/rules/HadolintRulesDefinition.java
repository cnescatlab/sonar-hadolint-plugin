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

import fr.cnes.sonar.plugins.hadolint.languages.DockerfileLanguage;
import fr.cnes.sonar.plugins.hadolint.settings.HadolintPluginProperties;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Specific Hadolint rules definition provided by resource file.
 */
public class HadolintRulesDefinition implements RulesDefinition {

	/**
	 * Define Hadolint rules in SonarQube thanks to xml configuration files.
	 *
	 * @param context SonarQube context.
	 */
	@Override
	public void define(Context context) {
		createRepository(context, DockerfileLanguage.KEY);
	}
	/**
	 * Create repositories for each language.
	 *
	 * @param context SonarQube context.
	 * @param language Key of the language.
	 */
	protected void createRepository(final Context context, final String language) {
		// Create a repository to put rules inside.
		final NewRepository repository = context
                .createRepository(getRepositoryKeyForLanguage(language), language)
                .setName(getRepositoryName());

		// Create loader for Hadolint & ShellCheck
		RuleMetadataLoader metadataLoaderHadolint = new RuleMetadataLoader(HadolintPluginProperties.HADOLINT_RULES_DEFINITION_FOLDER);
		RuleMetadataLoader metadataLoaderShellCheck = new RuleMetadataLoader(HadolintPluginProperties.SHELLCHECK_RULES_DEFINITION_FOLDER);

		// Load rules for Hadolint
		List<String> keys = new ArrayList<>();
        HadolintRepository.getHadolintRuleKeys().forEach(keys::add);
		metadataLoaderHadolint.addRulesByRuleKey(repository, keys);

		// Load rules for ShellCheck
		keys = new ArrayList<>();
		HadolintRepository.getShellCheckRuleKeys().forEach(keys::add);
		metadataLoaderShellCheck.addRulesByRuleKey(repository, keys);
		
		repository.done();
	}

	/**
     * Getter for repository key.
	 *
	 * @param language Key of the related language.
     * @return A string "language-key".
     */
    public static String getRepositoryKeyForLanguage(final String language) {
        return language;
    }

    /**
     * Getter for repository name.
     *
     * @return A string.
     */
    public static String getRepositoryName() {
        return HadolintPluginProperties.HADOLINT_NAME;
    }
}

