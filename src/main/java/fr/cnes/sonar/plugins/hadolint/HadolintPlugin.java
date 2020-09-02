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
package fr.cnes.sonar.plugins.hadolint;

import fr.cnes.sonar.plugins.hadolint.check.HadolintSensor;
import fr.cnes.sonar.plugins.hadolint.languages.*;
import fr.cnes.sonar.plugins.hadolint.rules.HadolintRulesDefinition;
import fr.cnes.sonar.plugins.hadolint.settings.HadolintPluginProperties;
import org.sonar.api.Plugin;

/**
 * This class is the entry point for all extensions.
 */
public class HadolintPlugin implements Plugin {

	/**
	 * Define all extensions implemented by the plugin.
	 *
	 * @param context SonarQube context.
	 */
	@Override
	public void define(Context context) {
		// Setting plugin Hadolint
		context.addExtension(DockerfileLanguage.class);
		context.addExtension(HadolintQualityProfiles.class);
		context.addExtensions(HadolintPluginProperties.getProperties());

		// Rules definition
		context.addExtension(HadolintRulesDefinition.class);
		
		// Sonar scanner extension
		context.addExtension(HadolintSensor.class);
	}
}
