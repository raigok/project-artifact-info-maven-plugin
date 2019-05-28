package ee.originaal.maven.plugins.projectartifactinfo;

import static ee.originaal.maven.plugins.projectartifactinfo.MavenObjectsFactory.newArtifact;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProjectArtifactInfoTest {

    private static final String ARTIFACT_TYPE = "jar";
    private static final String CLASSIFIER = "sources";
    private static final String ARTIFACT_FILE = "/tmp/path/to/artifact/file.jar";
    private static final String ARTIFACT_INFO_WITHOUT_CLASSIFIER_CSV = "jar,,/tmp/path/to/artifact/file.jar";
    private static final String ARTIFACT_INFO_WITH_CLASSIFIER_CSV = "jar,sources,/tmp/path/to/artifact/file.jar";

    @Test
    void printsInCsvFormatWithoutClassifier() {
        ProjectArtifactInfo artifactInfo = new ProjectArtifactInfo(newArtifact(ARTIFACT_TYPE, null, ARTIFACT_FILE));

        assertThat(artifactInfo.toString()).isEqualTo(ARTIFACT_INFO_WITHOUT_CLASSIFIER_CSV);
    }

    @Test
    void parsesArtifactInfoWithoutClassifier() {
        ProjectArtifactInfo artifactInfo = new ProjectArtifactInfo(newArtifact(ARTIFACT_TYPE, null, ARTIFACT_FILE));

        assertThat(ProjectArtifactInfo.parse(ARTIFACT_INFO_WITHOUT_CLASSIFIER_CSV)).isEqualToComparingFieldByField(artifactInfo);
    }

    @Test
    void printsInCsvFormatWithClassifier() {
        ProjectArtifactInfo artifactInfo = new ProjectArtifactInfo(newArtifact(ARTIFACT_TYPE, CLASSIFIER, ARTIFACT_FILE));

        assertThat(artifactInfo.toString()).isEqualTo(ARTIFACT_INFO_WITH_CLASSIFIER_CSV);
    }

    @Test
    void parsesArtifactInfoWithClassifier() {
        ProjectArtifactInfo artifactInfo = new ProjectArtifactInfo(newArtifact(ARTIFACT_TYPE, CLASSIFIER, ARTIFACT_FILE));

        assertThat(ProjectArtifactInfo.parse(ARTIFACT_INFO_WITH_CLASSIFIER_CSV)).isEqualToComparingFieldByField(artifactInfo);
    }
}