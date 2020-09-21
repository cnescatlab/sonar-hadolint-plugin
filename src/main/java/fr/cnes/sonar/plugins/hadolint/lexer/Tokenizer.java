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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class used to create SonarQube Tokens on a source file
 */
public class Tokenizer {

    /**
     * Returns a list of all Tokens created on a Dockerfile
     * 
     * @param dockerfile Source file to put tokens on
     * @return List of all generated token
     */
    public List<DockerfileToken> tokenize(String dockerfile) {
        List<Token> tokenList = DockerfileLexer.create().lex(dockerfile);

        // remove last token (EOF token)
        List<Token> cloneTokenList = new ArrayList<>(tokenList);
        cloneTokenList.remove(cloneTokenList.size() - 1);

        return cloneTokenList.stream().map(DockerfileToken::new).collect(Collectors.toList());
    }
}