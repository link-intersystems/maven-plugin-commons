package com.link_intersystems.maven.plugin.test;

import com.link_intersystems.maven.plugin.test.extensions.MojoTest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */

@ExtendWith(MojoTest.class)
@MavenTestProject("com/link_intersystems/maven/plugin/test/simpleProject")
class SimpleMojoTest {

    @Test
    void testPomInterpolation(@TestMojo(goal = "goal", debugEnabled = true) SimpleMojo mojo, MavenProject mavenProject) throws MojoExecutionException, MojoFailureException {
        File basedir = mavenProject.getBasedir();
        assertEquals(basedir.toString(), mojo.dir);
    }

    @Test
    void executeMojo(@TestMojo(goal = "goal", debugEnabled = true) SimpleMojo mojo) throws MojoExecutionException, MojoFailureException {
        assertNotNull(mojo, "Mojo should be resolved.");
        mojo.execute();
    }

    @Test
    void resolveMojoAndProject(@TestMojo(goal = "goal") SimpleMojo mojo, MavenProject mavenProject) throws MojoExecutionException, MojoFailureException {
        executeMojo(mojo);
        assertSame(mavenProject, mojo.mavenProject, "Injected project should be the same as the MavenProject parameter.");

        File basedir = mavenProject.getBasedir();
        File readme = new File(basedir, "src/main/resources/README.md");
        assertTrue(readme.exists());
    }

    @Test
    void resolveMojoProjectAndSession(@TestMojo(goal = "goal") SimpleMojo mojo, MavenProject mavenProject, MavenSession mavenSession) throws MojoExecutionException, MojoFailureException {
        executeMojo(mojo);
        assertSame(mavenProject, mojo.mavenProject, "Injected project should be the same as the MavenProject parameter.");
    }
}