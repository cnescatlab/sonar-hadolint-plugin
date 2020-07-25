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

import org.sonar.api.resources.AbstractLanguage;

/**
 * Declares language Dockerfile.
 */
public final class DockerfileLanguage extends AbstractLanguage {

	public static final String KEY = "dockerfile";
	public static final String NAME = "Dockerfile";
	public static final String[] INTERPRETERS = {};

	/**
	 * DockerfileCheck extension
	 *
	 * @param configuration Inject SonarQube configuration into this extension.
	 */
	public DockerfileLanguage() {
		super(DockerfileLanguage.KEY, DockerfileLanguage.NAME);
	}

	/**
	 * Returns the list of suffixes which should be associated to this language.
	 *
	 * @return A strings' array with file's suffixes.
	 */
	@Override
	public String[] getFileSuffixes() {
		return DockerfileLanguage.INTERPRETERS;
	}

	/**
	 * Assert obj is the same object as this.
	 *
	 * @param obj Object to compare with this.
	 * @return True if obj is this.
	 */
	@Override
	public boolean equals(Object obj) {
		return obj==this;
	}

	/**
	 * Override hashcode because equals is overridden.
	 *
	 * @return An integer hashcode.
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
