package ee.originaal.maven.plugins.projectartifactinfo;

import java.io.File;

import org.apache.maven.artifact.Artifact;

public class ProjectArtifactInfo {

    private static final String DELIMITER = ",";

    private final String type;
    private final String classifier;
    private final File file;

    ProjectArtifactInfo(Artifact artifact) {
        this(artifact.getType(), artifact.getClassifier(), artifact.getFile());
    }

    private ProjectArtifactInfo(String type, String classifier, File file) {
        this.type = type;
        this.classifier = classifier;
        this.file = file;
    }

    static ProjectArtifactInfo parse(String line) {
        if (line != null) {
            String[] split = line.split(DELIMITER, 3);
            if (split.length == 3) {
                return new ProjectArtifactInfo(split[0], split[1].isEmpty() ? null : split[1], new File(split[2]));
            }
        }

        throw new IllegalArgumentException("Illegal argument info line: \"" + line + "\". " +
                "Expected format: \"" + String.join(DELIMITER, "type", "classifier", "file") + "\"");
    }

    @Override
    public String toString() {
        return String.join(DELIMITER, type, classifier != null ? classifier : "", file.toString());
    }

    String getType() {
        return type;
    }

    String getClassifier() {
        return classifier;
    }

    File getFile() {
        return file;
    }
}
