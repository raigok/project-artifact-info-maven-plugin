package ee.originaal.maven.plugins.projectartifactinfo;

import static ee.originaal.maven.plugins.projectartifactinfo.MavenObjectsFactory.newArtifact;
import static ee.originaal.maven.plugins.projectartifactinfo.MavenObjectsFactory.newMavenProject;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.linesOf;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PersistMojoTest {

    private static final String ARTIFACTS_INFO_FILENAME = "project-artifacts.txt";
    private static final Artifact MAIN_POM_ARTIFACT_WITHOUT_FILE = newArtifact("pom", null);
    private static final Artifact MAIN_JAR_ARTIFACT = newArtifact("jar", null, "/tmp/path/to/artifact-1.0.0.jar");
    private static final Artifact ATTACHED_SOURCES_ARTIFACT = newArtifact("jar", "sources", "/tmp/path/to/artifact-sources-1.0.0.jar");
    private static final Artifact ATTACHED_TESTS_ARTIFACT = newArtifact("test-jar", "tests", "/tmp/path/to/artifact-tests-1.0.0.jar");

    @TempDir
    File projectBuildDirectory;
    private File artifactsInfoFile;

    @BeforeEach
    void setUp() {
        artifactsInfoFile = new File(projectBuildDirectory, ARTIFACTS_INFO_FILENAME);
    }

    @Test
    void persistsSingleJarArtifactInfoIntoFile() throws MojoExecutionException {
        newPersistMojo(newMavenProject(MAIN_JAR_ARTIFACT)).execute();

        assertThat(artifactsInfoFile).exists();
        assertThat(artifactsInfoFile).hasContent("jar,,/tmp/path/to/artifact-1.0.0.jar");
    }

    @Test
    void persistsMultipleArtifactsInfoIntoFile() throws MojoExecutionException {
        newPersistMojo(newMavenProject(MAIN_JAR_ARTIFACT, ATTACHED_SOURCES_ARTIFACT, ATTACHED_TESTS_ARTIFACT)).execute();

        assertThat(artifactsInfoFile).exists();
        assertThat(linesOf(artifactsInfoFile, UTF_8)).containsExactly(
                "jar,,/tmp/path/to/artifact-1.0.0.jar",
                "jar,sources,/tmp/path/to/artifact-sources-1.0.0.jar",
                "test-jar,tests,/tmp/path/to/artifact-tests-1.0.0.jar");
    }

    @Test
    void skipsWhenNoArtifactsToPersist() throws MojoExecutionException {
        newPersistMojo(newMavenProject(MAIN_POM_ARTIFACT_WITHOUT_FILE)).execute();

        assertThat(artifactsInfoFile).doesNotExist();
    }

    private PersistMojo newPersistMojo(MavenProject mavenProject) {
        PersistMojo persistMojo = new PersistMojo();
        persistMojo.setProject(mavenProject);
        persistMojo.setDirectory(projectBuildDirectory);
        persistMojo.setFilename(ARTIFACTS_INFO_FILENAME);
        return persistMojo;
    }
}