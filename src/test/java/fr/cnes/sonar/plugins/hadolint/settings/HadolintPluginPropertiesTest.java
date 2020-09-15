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

import org.junit.Assert;
import org.junit.Test;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class HadolintPluginPropertiesTest {

    // Class logger
    private Logger logger = Loggers.get(HadolintPluginPropertiesTest.class);

    @Test
    public void testPluginPropertiesDefinition() {
        List<PropertyDefinition> actual = HadolintPluginProperties.getProperties();
        assertEquals(1, actual.size());
        PropertyDefinition reportsPath = actual.get(0);
        assertEquals(HadolintPluginProperties.HADOLINT_NAME, reportsPath.category());
        assertEquals(HadolintPluginProperties.REPORT_PATH_KEY, reportsPath.key());
        assertEquals(HadolintPluginProperties.REPORT_PATH_DEFAULT, reportsPath.defaultValue());
    }

    @Test
    public void testGetDockerfileKeywords() {
        List<String> keywords = HadolintPluginProperties.getDockerfileKeywords();
        assertEquals(18, keywords.size());
    }

    @Test(expected = InvocationTargetException.class)
    public void testConstructorException() throws InvocationTargetException {
        Constructor<HadolintPluginProperties> constructor;
        try {
            constructor = HadolintPluginProperties.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException e) {
            logger.error("Exception found when calling the private constructor : ", e);
        }
    }
}