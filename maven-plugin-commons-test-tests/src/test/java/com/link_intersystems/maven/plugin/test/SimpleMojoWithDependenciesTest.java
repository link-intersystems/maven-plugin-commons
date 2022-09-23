package com.link_intersystems.maven.plugin.test;

import com.link_intersystems.maven.plugin.test.extensions.MojoTestExtension;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */

@ExtendWith(MojoTestExtension.class)
@MavenTestProject("com/link_intersystems/maven/plugin/test/with-dependencies")
class SimpleMojoWithDependenciesTest {

    @Test
    void requiresDependencyResolution(@TestMojo(goal = "goal", debugEnabled = true, requiresDependencyResolution = ResolutionScope.COMPILE) SimpleMojo mojo, MavenProject mavenProject) throws MojoExecutionException, MojoFailureException {
        List<Dependency> dependencies = mavenProject.getDependencies();
        assertNotNull(dependencies);
        assertEquals(1, dependencies.size());

        Set<Artifact> artifacts = mavenProject.getArtifacts();
        assertNotNull(artifacts);
        assertEquals(1, artifacts.size());

        Artifact artifact = artifacts.iterator().next();
        File file = artifact.getFile();
        assertNotNull(file);
        assertTrue(file.toString().endsWith("commons-codec-1.15.jar"));
    }

}