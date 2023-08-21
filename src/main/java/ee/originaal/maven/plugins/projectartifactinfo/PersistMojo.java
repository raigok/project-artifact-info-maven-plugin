package ee.originaal.maven.plugins.projectartifactinfo;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Persists project artifacts information (type, classifier and file path) into a file.
 *
 * @see ee.originaal.maven.plugins.projectartifactinfo.RestoreMojo
 */
@Mojo(name = "persist", threadSafe = true, defaultPhase = LifecyclePhase.PACKAGE)
public class PersistMojo extends AbstractProjectArtifactInfoMojo {

    public void execute() throws MojoExecutionException {
        List<Artifact> artifactsWithFile = concat(Stream.of(getProject().getArtifact()), getProject().getAttachedArtifacts().stream())
                .filter(artifact -> artifact != null && artifact.getFile() != null)
                .collect(toList());

        if (artifactsWithFile.size() > 0) {
            getDirectory().mkdirs();
            writeToFile(new File(getDirectory(), getFilename()), artifactsWithFile);
        }
    }

    private void writeToFile(File artifactInfoFile, List<Artifact> artifacts) throws MojoExecutionException {
        getLog().info("Storing artifact info in " + artifactInfoFile);

        List<String> linesToWrite = artifacts.stream()
                .peek(artifact -> getLog().info(format("Storing artifact: type=%s, classifier=%s, file=%s", artifact.getType(), artifact.getClassifier(), artifact.getFile())))
                .map(artifact -> new ProjectArtifactInfo(artifact).toString())
                .collect(toList());

        try {
            Files.write(artifactInfoFile.toPath(), linesToWrite, UTF_8);
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("Failed to store artifacts info in file %s", artifactInfoFile), e);
        }
    }
}
