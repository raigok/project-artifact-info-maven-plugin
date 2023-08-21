package ee.originaal.maven.plugins.projectartifactinfo;

import static ee.originaal.maven.plugins.projectartifactinfo.MavenObjectsFactory.newArtifact;
import static ee.originaal.maven.plugins.projectartifactinfo.MavenObjectsFactory.newMavenProject;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RestoreMojoTest {

    private static final String ARTIFACTS_INFO_FILENAME = "project-artifacts.txt";

    @TempDir
    File projectBuildDirectory;
    private File artifactsInfoFile;

    @BeforeEach
    void setUp() {
        artifactsInfoFile = new File(projectBuildDirectory, ARTIFACTS_INFO_FILENAME);
    }

    @Test
    void updatesMainArtifactFromFile() throws IOException, MojoExecutionException {
        MavenProject mavenProject = newMavenProject(newArtifact("jar", null));
        assertThat(mavenProject.getArtifact().getFile()).isNull();

        Files.write(artifactsInfoFile.toPath(), "jar,,/tmp/path/to/artifact-1.0.0.jar".getBytes(UTF_8));
        newRestoreMojo(mavenProject).execute();

        assertThat(mavenProject.getArtifact().getFile()).hasToString("/tmp/path/to/artifact-1.0.0.jar");
    }

    @Test
    void restoresMainAndAttachedArtifactsFromFile() throws IOException, MojoExecutionException {
        MavenProject mavenProject = newMavenProject(newArtifact("jar", null));

        Files.write(artifactsInfoFile.toPath(), asList(
                "jar,,/tmp/path/to/artifact-1.0.0.jar",
                "jar,sources,/tmp/path/to/artifact-sources-1.0.0.jar",
                "test-jar,tests,/tmp/path/to/artifact-tests-1.0.0.jar"));
        newRestoreMojo(mavenProject).execute();

        assertThat(mavenProject.getArtifact().getFile()).hasToString("/tmp/path/to/artifact-1.0.0.jar");
        assertThat(mavenProject.getAttachedArtifacts()).flatExtracting("type", "classifier", "file")
                .containsExactly(
                        "jar", "sources", new File("/tmp/path/to/artifact-sources-1.0.0.jar"),
                        "test-jar", "tests", new File("/tmp/path/to/artifact-tests-1.0.0.jar"));
    }

    @Test
    void skipsWhenNoArtifactsFilePresent() throws MojoExecutionException {
        MavenProject mavenProject = newMavenProject(newArtifact("pom", null));
        newRestoreMojo(mavenProject).execute();

        assertThat(artifactsInfoFile).doesNotExist();
        assertThat(mavenProject.getArtifact().getFile()).isNull();
        assertThat(mavenProject.getAttachedArtifacts()).isEmpty();
    }

    private RestoreMojo newRestoreMojo(MavenProject mavenProject) {
        RestoreMojo restoreMojo = new RestoreMojo();
        restoreMojo.setProject(mavenProject);
        restoreMojo.setDirectory(projectBuildDirectory);
        restoreMojo.setFilename(ARTIFACTS_INFO_FILENAME);
        restoreMojo.setProjectHelper(mockMavenProjectHelper());
        return restoreMojo;
    }

    private MavenProjectHelper mockMavenProjectHelper() {
        MavenProjectHelper mavenProjectHelper = mock(MavenProjectHelper.class);
        doAnswer(answer -> {
            MavenProject mavenProject = answer.getArgument(0, MavenProject.class);
            String type = answer.getArgument(1, String.class);
            String classifier = answer.getArgument(2, String.class);
            File file = answer.getArgument(3, File.class);
            mavenProject.addAttachedArtifact(newArtifact(type, classifier, file));
            return null;
        }).when(mavenProjectHelper).attachArtifact(any(MavenProject.class), anyString(), anyString(), any(File.class));
        return mavenProjectHelper;
    }
}