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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DockerfileLanguageTest {
    @Test
	public void testGetFileSuffixes() {
		DockerfileLanguage language = new DockerfileLanguage();
		String[] expected = new String[]{};
		assertEquals(1, language.getFileSuffixes().length);
	}

	@Test
	public void testEquals() {
		DockerfileLanguage language1 = new DockerfileLanguage();
		DockerfileLanguage language2 = new DockerfileLanguage();
		assertEquals(language1, language1);
		assertNotEquals(language1, language2);
	}

	@Test
	public void testHashCode() {
		int expected = new DockerfileLanguage().hashCode();
		int actual = -1317317732;
		assertEquals(expected, actual);
	}
}