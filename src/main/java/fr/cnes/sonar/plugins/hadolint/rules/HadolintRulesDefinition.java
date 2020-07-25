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
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Specific Hadolint rules definition provided by resource file.
 *
 * @author lequal
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

		// Get XML file describing rules for language.
		final InputStream rulesXml = this.getClass().getResourceAsStream(rulesDefinitionFilePath(language));
		// Add rules in repository.
		if (rulesXml != null) {
			final RulesDefinitionXmlLoader rulesLoader = new RulesDefinitionXmlLoader();
			rulesLoader.load(repository, rulesXml, StandardCharsets.UTF_8.name());
		}
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

    /**
     * Getter for the path to rules file.
     *
	 * @param language Key of the language.
     * @return A path in String format.
     */
	public String rulesDefinitionFilePath(final String language) {
		String path = "bad_file";

		if (DockerfileLanguage.KEY.equals(language)) {
			path = HadolintPluginProperties.PATH_TO_HADOLINT_RULES_XML;
		}

		return path;
	}
}

