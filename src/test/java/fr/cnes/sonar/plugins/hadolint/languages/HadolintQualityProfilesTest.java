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

import org.junit.Assert;
import org.junit.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

public class HadolintQualityProfilesTest {
    @Test
    public void testShouldCreateSonarWayProfile() {
        HadolintQualityProfiles profileDef = new HadolintQualityProfiles();
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        profileDef.define(context);
        Assert.assertNotNull(profileDef);
        Assert.assertEquals(1, context.profilesByLanguageAndName().keySet().size());
    }
}