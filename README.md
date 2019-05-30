project-artifact-info-maven-plugin
===

A [Maven][1] plugin for supporting execution of [install:install][2] and [deploy:deploy][3] goals outside of Maven build lifecycle. It is meant to be used in build pipelines, where project is built and deployed in separate steps.

### Getting started

Add `project-artifact-info-maven-plugin` plugin to your project's `pom.xml`:
```xml
<project>
    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>ee.originaal.maven.plugins</groupId>
                    <artifactId>project-artifact-info-maven-plugin</artifactId>
                    <version>LATEST</version>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```

Build your project and persist artifact info to disk (`target/project-artifacts.txt` by default)
```bash
mvn clean verify project-artifact-info:persist
```
Restore artifact info from disk (`target/project-artifacts.txt` by default) and install/deploy
```bash
mvn project-artifact-info:restore install:install
# or
mvn project-artifact-info:restore deploy:deploy
```

### Goals
Goal | Description
--- | ---
`project-artifact-info:persist` | Persists details (type, classifier and file path) of every built artifact to disk so that we can restore them later.
`project-artifact-info:restore` | Restores previously built artifacts' details (type, classifier and file path) from disk so that we can execute [install:install][2] and [deploy:deploy][3] without rebuilding the project.

### Optional parameters
Name | Type | Description
--- | --- | ---
`<filename>` | String | The name of the file where artifact details are stored<br>Default value is: `project-artifacts.txt`<br>User property is: `project-artifact-info.filename`
`<directory>` | File | The directory where artifact info file is stored<br>Default value is: `${project.build.directory}`<br>User property is: `project-artifact-info.directory`


[1]: http://maven.apache.org/
[2]: https://maven.apache.org/plugins/maven-install-plugin/install-mojo.html 
[3]: https://maven.apache.org/plugins/maven-deploy-plugin/deploy-mojo.html 
