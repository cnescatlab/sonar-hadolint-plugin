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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.server.rule.RulesDefinition.Context;
import org.sonar.api.server.rule.RulesDefinition.NewRepository;

public class HadolintRulesDefinitionTest {	
	@Test(expected = NoClassDefFoundError.class)
	public void testRulesCreation() {
		Context context = Mockito.mock(Context.class);
		NewRepository newRepository = Mockito.mock(NewRepository.class);
		Mockito.when(context.createRepository(DockerfileLanguage.KEY, DockerfileLanguage.KEY)).thenReturn(newRepository);
		HadolintRulesDefinition hadolintRulesDefinition = new HadolintRulesDefinition();
		// Does not work, throws exception about missing lib in SonarQube API
		hadolintRulesDefinition.define(context);
	}

	@Test
	public void testRulesDefinitionFilePath(){
		HadolintRulesDefinition hadolintRulesDefinition = new HadolintRulesDefinition();
		String expected = HadolintPluginProperties.PATH_TO_HADOLINT_RULES_XML;
		String actual = hadolintRulesDefinition.rulesDefinitionFilePath(DockerfileLanguage.KEY);
		assertEquals(expected, actual);
	}

	@Test
	public void testRulesDefinitionFilePathWithWrongName(){
		HadolintRulesDefinition hadolintRulesDefinition = new HadolintRulesDefinition();
		String expected = HadolintPluginProperties.PATH_TO_HADOLINT_RULES_XML;
		String actual = hadolintRulesDefinition.rulesDefinitionFilePath("Wrong parameter");
		assertNotEquals(expected, actual);
	}

	@Test
	public void testRepositoryKeyFromLanguage(){
		String expected = DockerfileLanguage.KEY;
		String actual = HadolintRulesDefinition.getRepositoryKeyForLanguage(DockerfileLanguage.KEY);
		assertEquals(expected, actual);
	}

	@Test
	public void testRepositoryName(){
		String expected = HadolintPluginProperties.HADOLINT_NAME;
		String actual = HadolintRulesDefinition.getRepositoryName();
		assertEquals(expected, actual);
	}
}