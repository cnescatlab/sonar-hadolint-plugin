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
package fr.cnes.sonar.plugins.hadolint.check;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.rule.RuleKey;

import fr.cnes.sonar.plugins.hadolint.languages.DockerfileLanguage;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class HadolintSensorTest {

    private SensorContextTester context;

    private static final String PROJECT_KEY = "ProjectKey";
    private static final String DOCKERFILE_PATH = "Dockerfile";
    private static final String DOCKERFILE2_PATH = "Dockerfile_highlight";
    private static final String REPORT_PATH = "hadolint-report.xml";
    private static final String REPORT_ERROR_PATH = "hadolint-error.xml";
    private static final String PROPERTY_REPORT_PATH = "sonar.hadolint.reports.path";
    private static final String PROPERTY_DOCKERFILE_PATTERNS = "sonar.lang.patterns." + DockerfileLanguage.KEY;

    @Before
    public void prepare() throws URISyntaxException, IOException {
        // Prepare files for test filesystem
        String testFileDir = HadolintSensorTest.class.getClassLoader().getResource("project").getFile();

        // Create test fs
        DefaultFileSystem fs = new DefaultFileSystem(new File(testFileDir));
        fs.setEncoding(StandardCharsets.UTF_8);

        // Create Dockerfile for test and add it in fs
        DefaultInputFile dockerfile = TestInputFileBuilder.create(
            PROJECT_KEY,
            new File(""),
            new File(DOCKERFILE_PATH))
            .setType(InputFile.Type.MAIN)
            .setContents(new String(Files.readAllBytes(Paths.get(testFileDir + "/" + DOCKERFILE_PATH))))
            .build();
        fs.add(dockerfile);

        DefaultInputFile dockerfile2 = TestInputFileBuilder.create(
            PROJECT_KEY,
            new File(""),
            new File(DOCKERFILE2_PATH))
            .setType(InputFile.Type.MAIN)
            .setContents(new String(Files.readAllBytes(Paths.get(testFileDir + "/" + DOCKERFILE2_PATH))))
            .build();
        fs.add(dockerfile2);

        // Create hadolint report for test and add it in fs
        DefaultInputFile report = TestInputFileBuilder.create(
            PROJECT_KEY,
            new File(""),
            new File(REPORT_PATH))
            .setContents(new String(Files.readAllBytes(Paths.get(testFileDir + "/" + REPORT_PATH))))
            .build();
        fs.add(report);

        // Create faulty hadolint report for test and add it in fs
        DefaultInputFile reportError = TestInputFileBuilder.create(
            PROJECT_KEY,
            new File(""),
            new File(REPORT_ERROR_PATH))
            .setType(InputFile.Type.MAIN)
            .setContents(new String(Files.readAllBytes(Paths.get(testFileDir + "/" + REPORT_ERROR_PATH))))
            .build();
        fs.add(reportError);

        // Init test context
        context = SensorContextTester.create(fs.baseDir());
        context.setFileSystem(fs);
    }

    @Test
    public void testDescriptor() {
        // Set descriptor and check nominal values
        SensorDescriptor descriptor = Mockito.mock(SensorDescriptor.class);
        HadolintSensor sensor = new HadolintSensor();
        sensor.describe(descriptor);
        verify(descriptor).name("fr.cnes.sonar.plugins.hadolint.check.HadolintSensor");
        verify(descriptor).onlyOnFileType(InputFile.Type.MAIN);
        verify(descriptor).createIssuesForRuleRepositories(DockerfileLanguage.KEY);
    }
    
    @Test
	public void testNoReportFilesNoDockerfiles() {
        // Path for reports and no Dockerfiles found
        MapSettings settings = new MapSettings();
        settings.setProperty(PROPERTY_REPORT_PATH, "");
        settings.setProperty(PROPERTY_DOCKERFILE_PATTERNS, "**/*.fakedockerextension");
        context.setSettings(settings);

        // Check nothing was generated by the sensor
		final HadolintSensor sensor = new HadolintSensor();
        sensor.execute(context);
        assertEquals(0, context.allIssues().size());
        assertEquals(0, context.measures(PROJECT_KEY + ":" + DOCKERFILE_PATH).size());
        assertEquals(0, context.measures(PROJECT_KEY + ":" + DOCKERFILE2_PATH).size());
    }

    @Test
	public void testNormalWork() {
        // Path for reports and Dockerfiles are correct
        MapSettings settings = new MapSettings();
        settings.setProperty(PROPERTY_REPORT_PATH, REPORT_PATH);
        settings.setProperty(PROPERTY_DOCKERFILE_PATTERNS, DOCKERFILE_PATH);
        context.setSettings(settings);

        // Simulate active rule
        ActiveRules rules = Mockito.mock(ActiveRules.class);
        ActiveRule rule = Mockito.mock(ActiveRule.class);
        Mockito.when(rules.find(RuleKey.of(DockerfileLanguage.KEY, "DL3000"))).thenReturn(rule);
        context.setActiveRules(rules);

		final HadolintSensor sensor = new HadolintSensor();
        sensor.execute(context);
        
        // Check we get the expected issues and measures
        assertEquals(1, context.allIssues().size());
        assertEquals(2, context.measures(PROJECT_KEY + ":" + DOCKERFILE_PATH).size());
        assertEquals(0, context.measures(PROJECT_KEY + ":" + DOCKERFILE2_PATH).size());
    }

    @Test
	public void testMultipleDockerfiles() {
        // Path for reports and two patterns for two Dockerfile
        // One Dockerfile is not mentionned in Hadolint report
        MapSettings settings = new MapSettings();
        settings.setProperty(PROPERTY_REPORT_PATH, REPORT_PATH);
        settings.setProperty(PROPERTY_DOCKERFILE_PATTERNS, DOCKERFILE_PATH + "," + DOCKERFILE2_PATH);
        context.setSettings(settings);

        // Simulate active rule
        ActiveRules rules = Mockito.mock(ActiveRules.class);
        ActiveRule rule = Mockito.mock(ActiveRule.class);
        Mockito.when(rules.find(RuleKey.of(DockerfileLanguage.KEY, "DL3000"))).thenReturn(rule);
        context.setActiveRules(rules);

        // Check we get the expected issues and measures
		final HadolintSensor sensor = new HadolintSensor();
        sensor.execute(context);
        assertEquals(1, context.allIssues().size());
        assertEquals(2, context.measures(PROJECT_KEY + ":" + DOCKERFILE_PATH).size());
        assertEquals(2, context.measures(PROJECT_KEY + ":" + DOCKERFILE2_PATH).size());
    }

    @Test
	public void testRuleDeactivated() {
        // Path for reports and Dockerfiles are correct
        MapSettings settings = new MapSettings();
        settings.setProperty(PROPERTY_REPORT_PATH, REPORT_PATH);
        settings.setProperty(PROPERTY_DOCKERFILE_PATTERNS, DOCKERFILE_PATH);
        context.setSettings(settings);

        // Do not activate any rule
		final HadolintSensor sensor = new HadolintSensor();
        sensor.execute(context);

        // Check we have measure on Dockerfile but no issues
        assertEquals(0, context.allIssues().size());
        assertEquals(2, context.measures(PROJECT_KEY + ":" + DOCKERFILE_PATH).size());
        assertEquals(0, context.measures(PROJECT_KEY + ":" + DOCKERFILE2_PATH).size());
    }

    @Test
	public void testReportNotFound() {
        // Path for Dockerfile is correct
        // But path for report is incorrect
        MapSettings settings = new MapSettings();
        settings.setProperty(PROPERTY_REPORT_PATH, "fake-report.xml");
        settings.setProperty(PROPERTY_DOCKERFILE_PATTERNS, DOCKERFILE_PATH);
        context.setSettings(settings);

		final HadolintSensor sensor = new HadolintSensor();
        sensor.execute(context);

        // Check we have measures on Dockerfile but no issues
        assertEquals(0, context.allIssues().size());
        assertEquals(2, context.measures(PROJECT_KEY + ":" + DOCKERFILE_PATH).size());
        assertEquals(0, context.measures(PROJECT_KEY + ":" + DOCKERFILE2_PATH).size());
    }

    @Test
	public void testReportIsADirectory() {
        // Path for Dockerfile is correct
        // But path for report is a directory
        MapSettings settings = new MapSettings();
        settings.setProperty(PROPERTY_REPORT_PATH, ".");
        settings.setProperty(PROPERTY_DOCKERFILE_PATTERNS, DOCKERFILE_PATH);
        context.setSettings(settings);

		final HadolintSensor sensor = new HadolintSensor();
        sensor.execute(context);

        // Check we have measures on Dockerfile but no issues
        assertEquals(0, context.allIssues().size());
        assertEquals(2, context.measures(PROJECT_KEY + ":" + DOCKERFILE_PATH).size());
        assertEquals(0, context.measures(PROJECT_KEY + ":" + DOCKERFILE2_PATH).size());
    }

    @Test
	public void testDockerfileNotFound() {
        // Path for Dockerfile is incorrect
        // But path for report is correct
        MapSettings settings = new MapSettings();
        settings.setProperty(PROPERTY_REPORT_PATH, REPORT_PATH);
        settings.setProperty(PROPERTY_DOCKERFILE_PATTERNS, "Dockerfile_not_exist");
        context.setSettings(settings);

		final HadolintSensor sensor = new HadolintSensor();
        sensor.execute(context);

        // Check we have no measures and no issues
        assertEquals(0, context.allIssues().size());
        assertEquals(0, context.measures(PROJECT_KEY + ":" + DOCKERFILE_PATH).size());
        assertEquals(0, context.measures(PROJECT_KEY + ":" + DOCKERFILE2_PATH).size());
    }

    @Test
	public void testSourceFileNotFound() {
        // Path for Dockerfiles and report is OK
        // But report points to a source file that does not exist
        MapSettings settings = new MapSettings();
        settings.setProperty(PROPERTY_REPORT_PATH, REPORT_ERROR_PATH);
        settings.setProperty(PROPERTY_DOCKERFILE_PATTERNS, DOCKERFILE_PATH);
        context.setSettings(settings);

        // Simulate activation of a rule
        ActiveRules rules = Mockito.mock(ActiveRules.class);
        ActiveRule rule = Mockito.mock(ActiveRule.class);
        Mockito.when(rules.find(RuleKey.of(DockerfileLanguage.KEY, "Hadolint.DL3000"))).thenReturn(rule);
        context.setActiveRules(rules);

		final HadolintSensor sensor = new HadolintSensor();
        sensor.execute(context);

        // Check we have measures on Dockerfile but no issues
        assertEquals(0, context.allIssues().size());
        assertEquals(2, context.measures(PROJECT_KEY + ":" + DOCKERFILE_PATH).size());
        assertEquals(0, context.measures(PROJECT_KEY + ":" + DOCKERFILE2_PATH).size());
    }
    
}