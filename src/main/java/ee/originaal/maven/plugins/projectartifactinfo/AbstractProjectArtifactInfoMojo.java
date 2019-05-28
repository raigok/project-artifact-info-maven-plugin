package ee.originaal.maven.plugins.projectartifactinfo;

import java.io.File;

import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

abstract class AbstractProjectArtifactInfoMojo extends AbstractMojo {

    /**
     * Directory where project artifacts info file will be written
     */
    @Parameter(property = "project-artifact-info.directory", defaultValue = "${project.build.directory}", required = true)
    private File directory;

    /**
     * Project artifacts info file
     */
    @Parameter(property = "project-artifact-info.filename", defaultValue = "project-artifacts.txt", required = true)
    private String filename;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    File getDirectory() {
        return directory;
    }

    @VisibleForTesting
    void setDirectory(File directory) {
        this.directory = directory;
    }

    String getFilename() {
        return filename;
    }

    @VisibleForTesting
    void setFilename(String filename) {
        this.filename = filename;
    }

    MavenProject getProject() {
        return project;
    }

    @VisibleForTesting
    void setProject(MavenProject project) {
        this.project = project;
    }
}
