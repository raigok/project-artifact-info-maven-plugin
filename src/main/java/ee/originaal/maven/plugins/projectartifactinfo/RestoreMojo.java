package ee.originaal.maven.plugins.projectartifactinfo;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProjectHelper;

/**
 * Re-attaches previously built artifact info to maven project so that downstream plugins
 * (i.e. install:install and deploy:deploy) could be used without rebuilding the project.
 *
 * @see ee.originaal.maven.plugins.projectartifactinfo.PersistMojo
 */
@Mojo(name = "restore", threadSafe = true)
public class RestoreMojo extends AbstractProjectArtifactInfoMojo {

    @Component
    private MavenProjectHelper projectHelper;

    public void execute() throws MojoExecutionException {
        File artifactInfoFile = new File(getDirectory(), getFilename());

        if (artifactInfoFile.exists()) {
            readFromFile(artifactInfoFile).forEach(this::attachArtifact);
        }
    }

    private List<ProjectArtifactInfo> readFromFile(File artifactInfoFile) throws MojoExecutionException {
        getLog().info("Restoring artifact info from " + artifactInfoFile);

        try {
            return FileUtils.readLines(artifactInfoFile, UTF_8).stream()
                    .map(ProjectArtifactInfo::parse)
                    .collect(toList());
        } catch (IllegalArgumentException | IOException e) {
            throw new MojoExecutionException("Failed to read artifact info from '" + artifactInfoFile + "'", e);
        }
    }

    private void attachArtifact(ProjectArtifactInfo artifactInfo) {
        if (isMainArtifact(artifactInfo)) {
            if (getProject().getArtifact().getFile() == null) {
                logArtifactInfo("Attaching main artifact:", artifactInfo);
                getProject().getArtifact().setFile(artifactInfo.getFile());
            }
        } else {
            logArtifactInfo("Attaching artifact:", artifactInfo);
            projectHelper.attachArtifact(getProject(), artifactInfo.getType(), artifactInfo.getClassifier(), artifactInfo.getFile());
        }
    }

    private boolean isMainArtifact(ProjectArtifactInfo artifactInfo) {
        Artifact projectArtifact = getProject().getArtifact();
        return projectArtifact != null
                && Objects.equals(projectArtifact.getType(), artifactInfo.getType())
                && Objects.equals(projectArtifact.getClassifier(), artifactInfo.getClassifier());
    }

    private void logArtifactInfo(String message, ProjectArtifactInfo artifactInfo) {
        getLog().info(format("%s type=%s, classifier=%s, file=%s", message, artifactInfo.getType(), artifactInfo.getClassifier(), artifactInfo.getFile()));
    }

    @VisibleForTesting
    void setProjectHelper(MavenProjectHelper projectHelper) {
        this.projectHelper = projectHelper;
    }
}
