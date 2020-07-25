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

/**
 * Class used to unmarshal hadolint xml file.
 *
 * It contains an issue or a metric.
 */
@XStreamAlias("error")
public class CheckstyleError {

    @XStreamAlias("line")
   	@XStreamAsAttribute
    private String line;

    @XStreamAlias("column")
   	@XStreamAsAttribute
    private String column;

    @XStreamAlias("severity")
   	@XStreamAsAttribute
    private String severity;

    @XStreamAlias("message")
   	@XStreamAsAttribute
    private String message;

    @XStreamAlias("source")
   	@XStreamAsAttribute
    private String source;

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}
