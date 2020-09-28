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

import fr.cnes.sonar.plugins.hadolint.lexer.DockerfileToken;
import fr.cnes.sonar.plugins.hadolint.lexer.DockerfileTokenType;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TokenizerTest {

    private Tokenizer tokenizer;
    
    @Before
    public void prepare() {
        tokenizer = new Tokenizer();
    }

    @Test
    public void testComment() {
        String content = "# Ceci est un commentaire";
        List<DockerfileToken> tokens = this.tokenizer.tokenize(content);

        assertEquals(1, tokens.size());

        DockerfileToken token = tokens.get(0);

        assertEquals(DockerfileTokenType.COMMENT, token.getType());
        assertEquals(DockerfileTokenType.COMMENT.getName(), token.getType().getName());
        assertEquals(DockerfileTokenType.COMMENT.getValue(), token.getType().getValue());
        assertEquals(1, token.getStartLine().intValue());
        assertEquals(1, token.getEndLine().intValue());
        assertEquals(0, token.getStartColumn().intValue());
        assertEquals(25, token.getEndColumn().intValue());
        assertEquals(content, token.getText());
    }

    @Test
    public void testKeyword() {
        String content = "RUN some-command";
        List<DockerfileToken> tokens = this.tokenizer.tokenize(content);

        assertEquals(1, tokens.size());

        DockerfileToken token = tokens.get(0);

        assertEquals(DockerfileTokenType.KEYWORD, token.getType());
        assertEquals(1, token.getStartLine().intValue());
        assertEquals(1, token.getEndLine().intValue());
        assertEquals(0, token.getStartColumn().intValue());
        assertEquals(3, token.getEndColumn().intValue());
        assertEquals("RUN", token.getText());
    }

    @Test
    public void testString() {
        String content = "\"Some string.\"";
        List<DockerfileToken> tokens = this.tokenizer.tokenize(content);

        assertEquals(1, tokens.size());

        DockerfileToken token = tokens.get(0);

        assertEquals(DockerfileTokenType.STRING, token.getType());
        assertEquals(1, token.getStartLine().intValue());
        assertEquals(1, token.getEndLine().intValue());
        assertEquals(0, token.getStartColumn().intValue());
        assertEquals(14, token.getEndColumn().intValue());
        assertEquals(content, token.getText());
    }

    @Test
    public void testAllTokens() {
        String content = "RUN some-command \"parameters\" # with inline comment";
        List<DockerfileToken> tokens = this.tokenizer.tokenize(content);

        assertEquals(3, tokens.size());

        DockerfileToken token = tokens.get(0);

        assertEquals(DockerfileTokenType.KEYWORD, token.getType());
        assertEquals(1, token.getStartLine().intValue());
        assertEquals(1, token.getEndLine().intValue());
        assertEquals(0, token.getStartColumn().intValue());
        assertEquals(3, token.getEndColumn().intValue());
        assertEquals("RUN", token.getText());

        token = tokens.get(1);

        assertEquals(DockerfileTokenType.STRING, token.getType());
        assertEquals(1, token.getStartLine().intValue());
        assertEquals(1, token.getEndLine().intValue());
        assertEquals(17, token.getStartColumn().intValue());
        assertEquals(29, token.getEndColumn().intValue());
        assertEquals("\"parameters\"", token.getText());

        token = tokens.get(2);

        assertEquals(DockerfileTokenType.COMMENT, token.getType());
        assertEquals(1, token.getStartLine().intValue());
        assertEquals(1, token.getEndLine().intValue());
        assertEquals(30, token.getStartColumn().intValue());
        assertEquals(51, token.getEndColumn().intValue());
        assertEquals("# with inline comment", token.getText());
    }
    
}
