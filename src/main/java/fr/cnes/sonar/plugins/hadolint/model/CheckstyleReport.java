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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to unmarshal hadolint xml file.
 *
 * It contains meta data about the analyzed project.
 *
 * @author lequal
 */
@XStreamAlias("checkstyle")
@XStreamInclude(CheckstyleFile.class)
public class CheckstyleReport {

    @XStreamAlias("version")
   	@XStreamAsAttribute
    private String version;

    @XStreamImplicit(itemFieldName = "file")
    public List<CheckstyleFile> dockerfiles;

    /**
     * Getter for accessing analyzed files (sources).
     * @return A list of CheckstyleFile.
     */
    public List<CheckstyleFile> getCheckstyleFiles() {
        // Retrieve files
        List<CheckstyleFile> files;
        if(dockerfiles !=null) {
            files = dockerfiles;
        } else {
            files = new ArrayList<>();
        }
        return files;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
