package ee.originaal.maven.plugins.projectartifactinfo;

import java.io.File;
import java.util.stream.Stream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

final class MavenObjectsFactory {

    private static final String GROUP_ID = "group-id";
    private static final String ARTIFACT_ID = "artifact-id";
    private static final String VERSION = "1.0.0";
    private static final String COMPILE = "compile";

    private MavenObjectsFactory() {
    }

    static Artifact newArtifact(String artifactType, String classifier) {
        return newArtifact(artifactType, classifier, (File) null);
    }

    static Artifact newArtifact(String artifactType, String classifier, String artifactFile) {
        File file = artifactFile != null ? new File(artifactFile) : null;
        return newArtifact(artifactType, classifier, file);
    }

    static Artifact newArtifact(String artifactType, String classifier, File artifactFile) {
        Artifact artifact = new DefaultArtifact(GROUP_ID, ARTIFACT_ID, VERSION, COMPILE, artifactType, classifier, new DefaultArtifactHandler());
        artifact.setFile(artifactFile);
        return artifact;
    }

    static MavenProject newMavenProject(Artifact mainArtifact, Artifact... attachedArtifacts) {
        MavenProject mavenProject = new MavenProject(new Model());
        mavenProject.setArtifact(mainArtifact);
        Stream.of(attachedArtifacts).forEach(mavenProject::addAttachedArtifact);
        return mavenProject;
    }
}
