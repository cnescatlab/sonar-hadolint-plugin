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

import org.junit.Assert;
import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;

public class HadolintPluginTest {

    private static final Version VERSION_7_9 = Version.create(7, 9);

    @Test
	public void test_extensions_are_all_set() {
		HadolintPlugin hadolintPlugin = new HadolintPlugin();
		SonarRuntime runtime = SonarRuntimeImpl.forSonarQube(VERSION_7_9, SonarQubeSide.SERVER, SonarEdition.COMMUNITY);
		Plugin.Context context = new Plugin.Context(runtime);
		hadolintPlugin.define(context);
		Assert.assertEquals(6, context.getExtensions().size());
	}    
}