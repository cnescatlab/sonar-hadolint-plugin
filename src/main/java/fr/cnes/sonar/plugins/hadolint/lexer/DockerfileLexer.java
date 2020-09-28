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

import fr.cnes.sonar.plugins.hadolint.settings.HadolintPluginProperties;

import com.sonar.sslr.impl.Lexer;

import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.regexp;

/**
 * Dockerfile Lexer to identify specific parts of the code
 */
public final class DockerfileLexer {

    // Patterns to match common non code characters
    private static final String NEW_LINE = "(?:\r\n|\r|\n|\f)";
    private static final String WHITESPACE = "[\t\n\f\r ]";
    private static final String HEX_DIGIT = "0-9a-fA-F";
    private static final String ESCAPE = "(?:\\\\[" + HEX_DIGIT + "]{1,6}" + WHITESPACE + "?)|\\[^\r\n\f" + HEX_DIGIT + "]";

    // Pattern to match comments
    private static final String COMMENT = "#[^\n\r\f]*+";

    // Patterns to match strings
    private static final String DOUBLE_QUOTE_STRING = "~?+\"(?:[^\"\\\\\r\n\f]|" + ESCAPE + "|\\\\" + NEW_LINE + ")*+\"";
    private static final String SINGLE_QUOTE_STRING = "~?+'(?:[^'\\\\\r\n\f]|" + ESCAPE + "|\\\\" + NEW_LINE + ")*+'";

    // Pattern to match Dockerfile specific keywords
    private static final String KEYWORD = "^(" + String.join("|", HadolintPluginProperties.getDockerfileKeywords()) + ")";

    // Hide constructor
    private DockerfileLexer() {}

    /**
     * Create the Dockerfile Lexer to be used in the Tokenizer
     */
    public static Lexer create() {
        return Lexer.builder()
            .withFailIfNoChannelToConsumeOneCharacter(false)

            // register all patterns and the associated token
            .withChannel(regexp(DockerfileTokenType.KEYWORD, KEYWORD))
            .withChannel(regexp(DockerfileTokenType.COMMENT, COMMENT))
            .withChannel(regexp(DockerfileTokenType.STRING, DOUBLE_QUOTE_STRING))
            .withChannel(regexp(DockerfileTokenType.STRING, SINGLE_QUOTE_STRING))

            .build();
    }
    
}
