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
package fr.cnes.sonar.plugins.hadolint.lexer;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import org.sonarsource.analyzer.commons.TokenLocation;

/**
 * Utility class describing a SonarQube Token inside a Dockerfile
 */
public class DockerfileToken {

    private DockerfileTokenType type;
    private String text;
    private Integer startLine;
    private Integer startColumn;
    private Integer endLine;
    private Integer endColumn;

    // Constructor
    public DockerfileToken(Token token) {
        TokenType tokenType = token.getType();
        this.type = (DockerfileTokenType)tokenType;
        this.text = token.getValue();

        TokenLocation tokenLocation = new TokenLocation(token.getLine(), token.getColumn(), token.getValue());
        this.startLine = tokenLocation.startLine();
        this.startColumn = tokenLocation.startLineOffset();
        this.endLine = tokenLocation.endLine();
        this.endColumn = tokenLocation.endLineOffset();
    }

    // Getter for type
    public DockerfileTokenType getType() {
        return this.type;
    }

    // Getter for text
    public String getText() {
        return this.text;
    }

    // Getter for startLine
    public Integer getStartLine() {
        return this.startLine;
    }

    // Getter for endLine
    public Integer getEndLine() {
        return this.endLine;
    }

    // Getter for startColumn
    public Integer getStartColumn() {
        return this.startColumn;
    }

    // Getter for endColumn
    public Integer getEndColumn() {
        return this.endColumn;
    }
}
