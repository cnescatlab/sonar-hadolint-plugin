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
package fr.cnes.sonar.plugins.hadolint.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.sonar.api.utils.WildcardPattern;

/**
 * Utility class to retrieve all files matching a wildcard pattern
 */
public class DirectoryScanner {

    private final File baseDir;
    private final WildcardPattern pattern;

    public DirectoryScanner(File baseDir, WildcardPattern pattern) {
        this.baseDir = baseDir;
        this.pattern = pattern;
    }

    public List<String> getIncludedFiles() {
        final String baseDirAbsolutePath = baseDir.getAbsolutePath();
        IOFileFilter fileFilter = new IOFileFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return accept(new File(dir, name));
            }

            @Override
            public boolean accept(File file) {
                String path = file.getAbsolutePath();
                path = path.substring(Math.min(baseDirAbsolutePath.length(), path.length()));
                return pattern.match(FilenameUtils.separatorsToUnix(path));
            }
        };

        List<File> fList = new ArrayList<>(FileUtils.listFiles(baseDir, fileFilter, TrueFileFilter.INSTANCE));

        return fList.stream().map(file -> file.getPath()).collect(Collectors.toList());
    }

}
