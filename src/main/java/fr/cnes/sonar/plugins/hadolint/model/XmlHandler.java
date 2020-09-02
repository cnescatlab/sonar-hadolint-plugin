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
package fr.cnes.sonar.plugins.hadolint.model;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import com.thoughtworks.xstream.converters.basic.NullConverter;
import com.thoughtworks.xstream.converters.basic.StringConverter;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import java.io.InputStream;
import java.util.Collection;

/**
 * Class used to unmarshal ShellCheck xml file (results and rules definition).
 *
 * It contains useful methods to handle xml files.
 *
 * @author lequal
 */
public class XmlHandler {

    /**
     * Private constructor for this utility class.
     */
    private XmlHandler(){}

    /**
     * This method use XStream to unmarshal XML report: it transform simply
     * XML into our Java Object by reading annotations on model classes.
     *
     * @param file Stream of the xml file to import as Java Objects.
     * @param cls Destination class for unmarshalling.
     * @return AnalysisReport: the main structure of the report.
     */
    public static Object unmarshal(final InputStream file, final Class<?> cls){
        XStream xStream = new XStream() {
            @Override
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new MapperWrapper(next) {
                    @Override
                    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
                        return (definedIn != Object.class) && super.shouldSerializeMember(definedIn, fieldName);
                    }
                };
            }
            // only register the converters we need
            // other converters generate a private access warning in the console on Java9+...
            @Override
            protected void setupConverters() {
                registerConverter(new NullConverter(), PRIORITY_VERY_HIGH);
                registerConverter(new StringConverter(), PRIORITY_NORMAL);
                registerConverter(new CollectionConverter(getMapper()), PRIORITY_NORMAL);
                registerConverter(new ReflectionConverter(getMapper(), getReflectionProvider()), PRIORITY_VERY_LOW);
            }
        };

        // Configure XStream security feature to avoid warnings
        // clear out existing permissions and set own ones
        xStream.addPermission(NoTypePermission.NONE);
        // allow some basics
        xStream.addPermission(NullPermission.NULL);
        xStream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xStream.allowTypeHierarchy(Collection.class);
        // allow any type from the same package
        xStream.allowTypesByWildcard(new String[] {
            "fr.cnes.sonar.plugins.hadolint.**"
        });

        xStream.processAnnotations(cls);

        return xStream.fromXML(file);
    }

}
